package uk.ac.cam.db538.dexter.android;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

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
    private EditText textInstrumentApkSize;
    private Button btnInstrument;
    private Button btnUninstall;
    private Button btnReplace;

    enum AppState {Uninstrumented, Instrumented, OriginalUninstalled};
    private AppState appState;

    BroadcastReceiver pkgRemoveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // do my stuff
            if (Intent.ACTION_PACKAGE_REMOVED.equals(intent)) {
                updateAppState();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(pkgRemoveReceiver);
    }

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
        textInstrumentApkSize = (EditText) rootView.findViewById(R.id.textInstrumentApkSize);
        btnInstrument = (Button) rootView.findViewById(R.id.buttonInstrument);
        btnUninstall = (Button) rootView.findViewById(R.id.buttonUninstall);
        btnReplace = (Button)rootView.findViewById(R.id.buttonReplace);

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

            btnUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri packageURI = Uri.parse("package:" + packageInfo.getPackageName());
                    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    startActivity(uninstallIntent);
                }
            });

            btnReplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File apk = getInstrumentedFile(packageInfo);
                    if (apk != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(pkgRemoveReceiver, filter);

        updateAppState();
    }

    private void updateAppState() {
        if (!getInstrumentedFile(packageInfo).exists())
            appState = AppState.Uninstrumented;
        else {
            try {
                getActivity().getPackageManager().getApplicationInfo(packageInfo.getPackageName(), 0);
                appState = AppState.Instrumented;
            } catch ( PackageManager.NameNotFoundException e) {
                appState = AppState.OriginalUninstalled;
            }
        }

        switch (appState) {
            case Uninstrumented:
                btnUninstall.setEnabled(false);
                btnReplace.setEnabled(false);
                break;
            case Instrumented:
                btnUninstall.setEnabled(true);
                btnReplace.setEnabled(false);
                break;
            case OriginalUninstalled:
                btnUninstall.setEnabled(false);
                btnReplace.setEnabled(true);
                break;
        }

        File apk = getInstrumentedFile(packageInfo);
        if (apk.exists()) {
            textInstrumentApkSize.setText(Long.toString(apk.length() / 1024) + " KB");
        } else {
            textInstrumentApkSize.setText("N/A");
        }
    }

}
