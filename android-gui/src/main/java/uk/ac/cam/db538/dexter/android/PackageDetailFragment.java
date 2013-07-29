package uk.ac.cam.db538.dexter.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * A fragment representing a single Package detail screen.
 * This fragment is either contained in a {@link PackageListActivity}
 * in two-pane mode (on tablets) or a {@link PackageDetailActivity}
 * on handsets.
 */
public class PackageDetailFragment extends Fragment {

    private ActivityManager activityManager;
    private PackageManager packageManager;

    private Package packageInfo;
    private File packageFile;

    private ImageView imgPackageIcon;
    private TextView textApplicationName;
    private EditText textPackageName;
    private EditText textPackageVersion;
    private EditText textLastUpdated;
    private EditText textApkPath;
    private EditText textApkSize;
    private Button btnInstrument;

    public PackageDetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = activity.getPackageManager();

        if (getArguments().containsKey(PACKAGE_NAME)) {
            String packageName = getArguments().getString(PACKAGE_NAME);
            try {
                packageInfo = new Package(packageManager, packageManager.getPackageInfo(packageName, 0));
                packageFile = packageInfo.getPackageFile();

                if (!packageFile.exists()) {
                    // TODO: show error message
                    throw new RuntimeException();
                } else if (!packageFile.canRead()) {
                    // TODO: show error message
                    throw new RuntimeException();
                }
            } catch (PackageManager.NameNotFoundException ex) {
                // package of given name not found
                // TODO: show error message
                throw new RuntimeException(ex);
            }
        } else {
            // intent does not contain package name
            // TODO: show error message
            throw new RuntimeException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_package_detail, container, false);

        imgPackageIcon = (ImageView) rootView.findViewById(R.id.imgPackageIcon);
        textApplicationName = (TextView) rootView.findViewById(R.id.textApplicationName);
        textPackageName = (EditText) rootView.findViewById(R.id.textPackage);
        textPackageVersion = (EditText) rootView.findViewById(R.id.textVersion);
        textLastUpdated = (EditText) rootView.findViewById(R.id.textLastUpdated);
        textApkPath = (EditText) rootView.findViewById(R.id.textApkPath);
        textApkSize = (EditText) rootView.findViewById(R.id.textApkSize);
        btnInstrument = (Button) rootView.findViewById(R.id.buttonInstrument);

        if (packageInfo != null) {
            Drawable icon = packageInfo.getApplicationIcon();
            String apkSize = Long.toString(packageFile.length() / 1024) + " KB";
            imgPackageIcon.setImageDrawable(icon);
            textApplicationName.setText(packageInfo.getApplicationName());
            textPackageName.setText(packageInfo.getPackageName());
            textPackageVersion.setText(packageInfo.getVersion());
            textLastUpdated.setText(packageInfo.getLastUpdated());
            textApkPath.setText(packageInfo.getPackageFile().getAbsolutePath());
            textApkSize.setText(apkSize);

            btnInstrument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    System.out.println("Heap size: " +
                        PackageDetailFragment.this.activityManager.getMemoryClass());
                    System.out.println("Large heap size: " +
                            PackageDetailFragment.this.activityManager.getLargeMemoryClass());

                    Intent detailIntent = new Intent(PackageDetailFragment.this.getActivity(),
                            InstrumentActivity.class);
                    detailIntent.putExtra(InstrumentActivity.PACKAGE_NAME,
                            PackageDetailFragment.this.packageInfo.getPackageName());
                    startActivity(detailIntent);
                }
            });
        }

        return rootView;
    }

    public static final String PACKAGE_NAME = "package_name";

}
