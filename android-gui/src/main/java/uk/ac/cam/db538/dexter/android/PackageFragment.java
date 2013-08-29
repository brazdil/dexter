package uk.ac.cam.db538.dexter.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by db538 on 7/29/13.
 */
public abstract class PackageFragment extends Fragment {

    protected Package extractArgsPackage() {
        if (getArguments().containsKey(PACKAGE_NAME)) {
            String packageName = getArguments().getString(PACKAGE_NAME);

            Activity activity = this.getActivity();
            PackageManager packageManager = activity.getPackageManager();

            try {
                Package packageInfo = new Package(packageManager, packageManager.getPackageInfo(packageName, 0));
                File packageFile = packageInfo.getPackageFile();

                if (!packageFile.exists()) {
                    // TODO: show error message
                    throw new RuntimeException("App file does not exist");
                } else if (!packageFile.canRead()) {
                    // TODO: show error message
                    throw new RuntimeException("App file cannot be read");
                }

                return packageInfo;

            } catch (PackageManager.NameNotFoundException ex) {
                // package of given name not found
                // TODO: show error message
                throw new RuntimeException(ex);
            }
        } else {
            // intent does not contain package name
            // TODO: show error message
            throw new RuntimeException("Arguments of fragment don't contain a package name");
        }
    }

    protected Bundle createPackageArgs(String packageName) {
        Bundle arguments = new Bundle();
        arguments.putString(PACKAGE_NAME, packageName);
        return arguments;
    }

    protected Bundle createPackageArgs(Package pkg) {
        return createPackageArgs(pkg.getPackageName());
    }

    public static void createPackageArgs(Intent intent, Package pkg) {
        intent.putExtra(PACKAGE_NAME, pkg.getPackageName());
    }

    public void setArgumentsFromPackageName(String pkgName) {
        setArguments(createPackageArgs(pkgName));
    }

    public void setArgumentsFromPackage(Package pkg) {
        setArguments(createPackageArgs(pkg));
    }

    public void setArgumentsFromIntent(Intent intent) {
        setArgumentsFromPackageName(intent.getExtras().getString(PACKAGE_NAME));
    }

    public static final String PACKAGE_NAME = "package_name";

    public File getInstrumentedFile(Package packageInfo) {
        return new File(getActivity().getDir("ready", Activity.MODE_PRIVATE) + "/" +
                        packageInfo.getPackageName() +  ".apk");
    }
}
