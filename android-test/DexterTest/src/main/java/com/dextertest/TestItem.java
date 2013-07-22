package com.dextertest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dextertest.tests.TestExerciser;

import java.lang.NegativeArraySizeException;
import java.lang.RuntimeException;

public class TestItem extends LinearLayout {

    private final TestExerciser testExerciser;

    private final TextView textTestName;
    private final CheckBox checkPassed;

    public TestItem(TestExerciser testExerciser, Context context, ViewGroup parent) {
        super(context);

        this.testExerciser = testExerciser;

        LayoutInflater inflater = LayoutInflater.from(context);
        this.addView(inflater.inflate(R.layout.item_testcase, parent, false));

        this.textTestName = (TextView) findViewById(R.id.testName);
        this.checkPassed = (CheckBox) findViewById(R.id.checkPassed);

        this.textTestName.setText(testExerciser.getName());
    }

    public void setChecked(boolean val) {
        this.checkPassed.setChecked(val);
    }
}
