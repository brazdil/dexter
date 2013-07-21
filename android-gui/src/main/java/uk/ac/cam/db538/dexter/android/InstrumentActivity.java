package uk.ac.cam.db538.dexter.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFileFromMemory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import uk.ac.cam.db538.dexter.apk.Apk;
import uk.ac.cam.db538.dexter.dex.AuxiliaryDex;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Pair;

public class InstrumentActivity extends Activity {

    private Typeface ttfRobotoLight;
    private Typeface ttfRobotoThin;

    private ActivityManager activityManager;
    private PackageManager packageManager;

    private PackageInfo packageInfo;
    private File packageFile;

    private TextView textStatus;
    private ProgressCircleView progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        this.getActionBar().hide();

        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = this.getPackageManager();

        textStatus = (TextView) findViewById(R.id.textStatus);
        progressCircle = (ProgressCircleView) findViewById(R.id.progressCircle);

        // load fonts
        ttfRobotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        ttfRobotoThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

        textStatus.setTypeface(ttfRobotoLight);
        progressCircle.setTypeface(ttfRobotoThin);

        if (savedInstanceState == null) {
            // initialize
            String packageName = getIntent().getStringExtra(PACKAGE_NAME);
            try {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
                packageFile = new File(packageInfo.applicationInfo.sourceDir);

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

        // initiate the instrumentation
        new Thread(workerInstrumentation).start();
    }

    private Runnable workerInstrumentation = new Runnable() {
        @Override
        public void run() {
            try {
                File localFile = new File(InstrumentActivity.this.getFilesDir(), "app.apk");
                DexterApplication thisApp = (DexterApplication) getApplication();

                setStatus("Loading files");

                /*
                 * We could load the DexFiles while framework is being loade, but that might
                 * use too much memory. This way, only one .dex file is being loaded at
                 * any point of time.
                 */

                thisApp.waitForHierarchy();

                FileUtils.copyFile(packageFile, localFile);
                DexFile fileApp = new DexFile(localFile);
                DexFile fileAux = new DexFileFromMemory(thisApp.getAssets().open("dexter_aux.dex"));

                Pair<RuntimeHierarchy, ClassRenamer> buildData = thisApp.getRuntimeHierarchy(fileApp, fileAux);
                RuntimeHierarchy hierarchy = buildData.getValA();
                ClassRenamer renamerAux = buildData.getValB();

                setStatus("Analyzing");
                Dex dexApp = new Dex(fileApp, hierarchy, new AuxiliaryDex(fileAux, hierarchy, renamerAux));

                buildData = null;
                fileApp = null;
                fileAux = null;
                renamerAux = null;
                System.gc();

                setStatus("Modifying");

//                terminalMessage("Instrumenting application");
//                dex.instrument(false);
//                terminalDone();

                setStatus("Assembling");
                byte[] fileApp_New = dexApp.writeToFile();

                setStatus("Signing");
                // Apk.produceAPK(localFile, localFile, null, fileApp_New);

                setStatus("DONE");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        private void setStatus(final String status) {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textStatus.setText(status + "...");
                }
            });
        }
    };

    public static final String PACKAGE_NAME = "package_name";
}
