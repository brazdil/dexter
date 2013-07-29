package uk.ac.cam.db538.dexter.android;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single Package detail screen.
 * This fragment is either contained in a {@link PackageListActivity}
 * in two-pane mode (on tablets) or a {@link PackageDetailActivity}
 * on handsets.
 */
public class PackageDetailFragment extends PackageFragment {

    private ActivityManager activityManager;
    private PackageManager packageManager;

    private Package packageInfo;

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
        packageInfo = extractArgsPackage();
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
            String apkSize = Long.toString(packageInfo.getPackageFile().length() / 1024) + " KB";
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
                    Intent detailIntent = new Intent(getActivity(), InstrumentActivity.class);
                    createPackageArgs(detailIntent, packageInfo);
                    startActivity(detailIntent);
                }
            });
        }

        return rootView;
    }
}
