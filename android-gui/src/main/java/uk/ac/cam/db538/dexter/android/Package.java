package uk.ac.cam.db538.dexter.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class Package {

    private PackageInfo pkg;
    private PackageManager pm;

    public Package(PackageManager pm, PackageInfo pkg) {
        this.pm = pm;
        this.pkg = pkg;
    }

    public String getApplicationName() {
        return pkg.applicationInfo.loadLabel(pm).toString();
    }

    public Drawable getApplicationIcon() {
        return pkg.applicationInfo.loadIcon(pm);
    }

    public String getPackageName() {
        return pkg.packageName;
    }

    public File getPackageFile() {
        return new File(pkg.applicationInfo.sourceDir);
    }

    public String getVersion() {
        return pkg.versionName;
    }

    public String getLastUpdated() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return df.format(new Date(pkg.lastUpdateTime));
    }

    public PackageInfo getPackageInfo() {
        return pkg;
    }
}
