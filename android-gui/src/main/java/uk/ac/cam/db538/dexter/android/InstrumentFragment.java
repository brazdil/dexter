package uk.ac.cam.db538.dexter.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.transform.taint.AuxiliaryDex;
import uk.ac.cam.db538.dexter.utils.Pair;

public class InstrumentFragment extends PackageFragment {

    private boolean running = false;

    private Package packageInfo;

    private TextView textStatus;
    private ProgressCircleView progressCircle;
    private ProgressBar memoryBar;

    private String currentStatus = "Loading...";
    private int currentProgress = 0;

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(InstrumentService.EXTRA_FINISHED, false)) {
                getActivity().finish();
                return;
            }

            String status = intent.getStringExtra(InstrumentService.EXTRA_STATUS);
            if (status != null)
                currentStatus = status;

            int progress = intent.getIntExtra(InstrumentService.EXTRA_PROGRESS, 0);
            if (progress != 0)
                currentProgress = progress;

            assignValues();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageInfo = extractArgsPackage();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        System.out.println("ActivityCreated");
        assignValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View thisView = inflater.inflate(R.layout.fragment_instrument, container, false);

        textStatus = (TextView) thisView.findViewById(R.id.textStatus);
        progressCircle = (ProgressCircleView) thisView.findViewById(R.id.progressCircle);
        memoryBar = (ProgressBar) thisView.findViewById(R.id.memoryBar);

        // load fonts
        Typeface ttfRobotoLight = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        Typeface ttfRobotoThin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Thin.ttf");

        textStatus.setTypeface(ttfRobotoLight);
        progressCircle.setTypeface(ttfRobotoThin);

        return thisView;
    }

    private void assignValues() {
        textStatus.setText(currentStatus);
        if (currentProgress < 0)
            progressCircle.setWaiting();
        else
            progressCircle.setValue(currentProgress);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter progressIntentFilter = new IntentFilter(InstrumentService.PROGRESS_ACTION);
        getActivity().registerReceiver(progressReceiver, progressIntentFilter);

        memoryTimer = new Timer();
        memoryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
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

        System.out.println("Resume");
        assignValues();

        synchronized (this) {
            if (!running) {
                Intent intent = new Intent(getActivity(), InstrumentService.class);
                intent.putExtra("PackageInfo", packageInfo.getPackageInfo());
                intent.putExtra("LocalFileTemp", new File(getActivity().getFilesDir(), "temp.apk").getAbsolutePath());
                intent.putExtra("LocalFileFinal", getInstrumentedFile(packageInfo).getAbsolutePath());
                getActivity().startService(intent);

                running = true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        memoryTimer.cancel();
        getActivity().unregisterReceiver(progressReceiver);
    }

    private Timer memoryTimer;
}
