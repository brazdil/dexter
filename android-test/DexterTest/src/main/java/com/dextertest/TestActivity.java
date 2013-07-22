package com.dextertest;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dextertest.tests.TestExerciser;
import com.dextertest.tests.TestList;

public class TestActivity extends Activity {

    private Button buttonExecute;
    private LinearLayout layoutTests;

    private TestExerciser[] tests;
    private TestItem[] testViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.buttonExecute = (Button) findViewById(R.id.buttonExec);
        this.layoutTests = (LinearLayout) findViewById(R.id.testListSpace);

        this.buttonExecute.setOnClickListener(onExecuteClicked);

        this.tests = TestList.getTestList();
        this.testViews = new TestItem[tests.length];

        for (int i = 0; i < tests.length; i++) {
            testViews[i] = new TestItem(tests[i], this, layoutTests);
            layoutTests.addView(testViews[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    private View.OnClickListener onExecuteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < tests.length; i++) {
                        final int index = i;
                        final boolean passed = tests[i].run();
                        TestActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testViews[index].setChecked(passed);
                                testViews[index].setBackgroundColor(passed ? Color.GREEN : Color.RED);
                            }
                        });
                    }
                }
            };
            t.start();
        }
    };
}
