package uk.ac.cam.db538.dexter.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PackageListItem extends LinearLayout {

    private Package packageInfo;

    private final ImageView imgPackageIcon;
    private final TextView textApplicationName;
    private final TextView textPackageName;

    public PackageListItem(Context context, ViewGroup parent) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        this.addView(inflater.inflate(R.layout.listitem_packages, parent, false));

        this.imgPackageIcon = (ImageView) this.findViewById(R.id.imgPackageIcon);
        this.textApplicationName = (TextView) this.findViewById(R.id.textPackageName);
        this.textPackageName = (TextView) this.findViewById(R.id.textApkPath);
    }

    public ImageView getImgPackageIcon() {
        return imgPackageIcon;
    }

    public TextView getTextPackageName() {
        return textPackageName;
    }

    public TextView getTextApplicationName() {
        return textApplicationName;
    }

    public Package getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(Package packageInfo) {
        this.packageInfo = packageInfo;

        textApplicationName.setText(packageInfo.getApplicationName());
        imgPackageIcon.setImageDrawable(packageInfo.getApplicationIcon());
        textPackageName.setText(packageInfo.getPackageName());
    }
}
