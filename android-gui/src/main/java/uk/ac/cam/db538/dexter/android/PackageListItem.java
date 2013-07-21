package uk.ac.cam.db538.dexter.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.NegativeArraySizeException;
import java.lang.RuntimeException;

public class PackageListItem extends LinearLayout {

    private PackageInfo packageInfo;

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

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;

        PackageManager pm = this.getContext().getPackageManager();

        try {
            CharSequence appName = pm
                .getResourcesForApplication(this.packageInfo.applicationInfo)
                .getText(this.packageInfo.applicationInfo.labelRes);
            this.textApplicationName.setText(appName);
        } catch (PackageManager.NameNotFoundException ex) {
            // TODO: handle this (app probably uninstalled)
            throw new RuntimeException(ex);
        } catch (Resources.NotFoundException ex) {
            // doesn't have a name specified?
            this.textApplicationName.setText("<no-name>");
        }

        Drawable pkgIcon = this.packageInfo.applicationInfo.loadIcon(pm);
        this.imgPackageIcon.setImageDrawable(pkgIcon);

        String pkgName = this.packageInfo.applicationInfo.packageName;
        this.textPackageName.setText(pkgName);

        String pkgApkPath = this.packageInfo.applicationInfo.sourceDir;
    }
}
