package uk.ac.cam.db538.dexter.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.File;

public class InstrumentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);
        getActionBar().hide();

        if (savedInstanceState == null) {
            // initialize
            InstrumentFragment fragment = new InstrumentFragment();
            fragment.setArgumentsFromIntent(getIntent());
            getSupportFragmentManager().beginTransaction()
                .add(R.id.instrumentation_container, fragment)
                .commit();
        }
    }

    @Override
    public void onBackPressed() {
        // suppress
    }
}
