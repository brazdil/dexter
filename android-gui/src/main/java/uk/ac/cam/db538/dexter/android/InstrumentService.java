package uk.ac.cam.db538.dexter.android;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import org.apache.commons.io.FileUtils;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.DexFileFromMemory;

import java.io.File;
import java.io.IOException;

import uk.ac.cam.db538.dexter.ProgressCallback;
import uk.ac.cam.db538.dexter.apk.Apk;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;
import uk.ac.cam.db538.dexter.utils.Pair;

/**
 * Created by rubin on 29/08/13.
 */
public class InstrumentService extends IntentService {

    public static final String PROGRESS_ACTION =
            "uk.ac.cam.db538.dexter.android.PROGRESS";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_FINISHED = "finished";

    public InstrumentService() {
        super("InstrumentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        try {

            final File packageFile = new File(intent.getStringExtra("PackageFile"));
            final File localFileTemp = new File(intent.getStringExtra("LocalFileTemp"));
            final File localFileFinal = new File(intent.getStringExtra("LocalFileFinal"));

            final DexterApplication thisApp = (DexterApplication) getApplication();

            broadcastStatus("Loading files...");
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

            broadcastStatus("Analyzing...");
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

            broadcastStatus("Modifying...");

//                 dex.instrument(false);

            broadcastStatus("Assembling...");
            byte[] fileApp_New = dexApp.writeToFile();

            broadcastStatus("Signing...");
            setWaiting();
            Apk.produceAPK(localFileTemp, localFileFinal, null, fileApp_New);
            localFileFinal.setReadable(true, false);
//                setStatus("Uninstalling...");
//                setWaiting();
//
//                Uri packageURI = Uri.parse("package:" + packageName);
//                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//                startActivity(uninstallIntent);


            broadcastFinished();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void broadcastProgress(int progress) {
        Intent localIntent = new Intent(PROGRESS_ACTION)
                .putExtra(EXTRA_PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void broadcastStatus(String status) {
        Intent localIntent = new Intent(PROGRESS_ACTION)
                .putExtra(EXTRA_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void broadcastFinished() {
        Intent localIntent = new Intent(PROGRESS_ACTION)
                .putExtra(EXTRA_FINISHED, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void setWaiting() {
        broadcastProgress(-1);
    }

    private ProgressCallback callbackProgressUpdate = new ProgressCallback() {
        @Override
        public void update(final int finished, final int outOf) {
        broadcastProgress(100 * finished / outOf);
        }
    };

}
