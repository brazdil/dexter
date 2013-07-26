package uk.ac.cam.db538.dexter.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFileFromMemory;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import uk.ac.cam.db538.dexter.ProgressCallback;
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
    private ProgressBar memoryBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        this.getActionBar().hide();

        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = this.getPackageManager();

        textStatus = (TextView) findViewById(R.id.textStatus);
        progressCircle = (ProgressCircleView) findViewById(R.id.progressCircle);
        memoryBar = (ProgressBar) findViewById(R.id.memoryBar);

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

    @Override
    public void onBackPressed() {
        // suppress
    }

    @Override
    protected void onResume() {
        super.onResume();
        memoryTimer = new Timer();
        memoryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                InstrumentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Runtime rt = Runtime.getRuntime();
                        long max = rt.maxMemory();
                        long allocated = rt.totalMemory();
                        long free = rt.freeMemory();

                        memoryBar.setProgress((int) (100L * (allocated - free) / max));
                    }
                });
            }
        }, 0, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        memoryTimer.cancel();
    }

    private ProgressCallback callbackProgressUpdate = new ProgressCallback() {
        @Override
        public void update(final int finished, final int outOf) {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressCircle.setValue(100 * finished / outOf);
                }
            });
        }
    };

    private Runnable workerInstrumentation = new Runnable() {
        @Override
        public void run() {
            setUiPriority_High();
            try {
                final String packageName = packageInfo.packageName;

                final File localFileTemp = new File(InstrumentActivity.this.getFilesDir(), "temp.apk");
                final File localFileFinal = new File(
                        InstrumentActivity.this.getDir("ready", MODE_PRIVATE),
                        packageName +  ".apk");
                final DexterApplication thisApp = (DexterApplication) getApplication();

                setStatus("Loading files...");
                setWaiting();

                /*
                 * We could load the DexFiles while framework is being loaded, but that might
                 * use too much memory. This way, only one .dex file is being loaded at
                 * any point of time.
                 */

                // thisApp.waitForHierarchy();

                FileUtils.copyFile(packageFile, localFileTemp);
                DexFile fileApp = new DexFile(localFileTemp);
                DexFile fileAux = new DexFileFromMemory(thisApp.getAssets().open("dexter_aux.dex"));

                Pair<RuntimeHierarchy, ClassRenamer> buildData = thisApp.getRuntimeHierarchy(fileApp, fileAux);
                RuntimeHierarchy hierarchy = buildData.getValA();
                ClassRenamer renamerAux = buildData.getValB();

                setStatus("Analyzing...");
                Dex dexApp = new Dex(
                    fileApp,
                    hierarchy,
                    new AuxiliaryDex(fileAux, hierarchy, renamerAux),
                    callbackProgressUpdate);

                buildData = null;
                fileApp = null;
                fileAux = null;
                renamerAux = null;
                System.gc();

                setStatus("Modifying...");

//                 dex.instrument(false);

                setStatus("Assembling...");
                byte[] fileApp_New = dexApp.writeToFile();

                setStatus("Signing...");
                setWaiting();
                Apk.produceAPK(localFileTemp, localFileFinal, null, fileApp_New);

//                setStatus("Uninstalling...");
//                setWaiting();
//
//                Uri packageURI = Uri.parse("package:" + packageName);
//                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//                startActivity(uninstallIntent);

                closeActivity();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        private void setUiPriority_High() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                }
            });
        }

        private void setStatus(final String status) {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textStatus.setText(status);
                }
            });
        }

        private void setWaiting() {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressCircle.setWaiting();
                }
            });
        }

        private void hideProgressCircle() {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressCircle.setVisibility(View.INVISIBLE);
                }
            });
        }

        private void closeActivity() {
            InstrumentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InstrumentActivity.this.finish();
                }
            });
        }
    };

    private Timer memoryTimer;

    public static final String PACKAGE_NAME = "package_name";
}
