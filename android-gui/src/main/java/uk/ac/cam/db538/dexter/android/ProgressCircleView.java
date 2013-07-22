package uk.ac.cam.db538.dexter.android;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by db538 on 7/19/13.
 */
public class ProgressCircleView extends View {

    private int value = 42;

    private int colorText = 0xff000000;
    private int colorCircle = 0xff000000; // ff2980b9, ff3498db;
    private int colorBackground = 0xffffffff;

    private Paint paintText;
    private Paint paintOuterCircle;
    private Paint paintInnerCircle;

    private float factorOuter = 0.7f;
    private float factorInner = 0.8f;
    private float factorText = 0.8f;
    private float factorPercent = 0.6f;

    private float radiusOuter;
    private float radiusInner;
    private float radiusText;
    private float sizeText;

    private float arcWaiting = 80.0f;
    private long durationWaiting = 1000;
    private ValueAnimator animatorWaiting = null;

    private PointF posCenter;
    private RectF rectOuterCircle;
    private RectF rectOuterCircle_extended;
    private RectF rectInnerCircle;
    private PointF posText_Value;
    private PointF posText_Percent;

    private boolean waitingMode = false;

    public ProgressCircleView(Context context) {
        super(context);

        init();
    }

    private int parseColor(TypedArray a, int attr, int def) {
        try {
            return a.getColor(attr, def);
        } catch (NumberFormatException ex) {
            return a.getResources().getColor(a.getResourceId(attr, android.R.color.black));
        }
    }

    public ProgressCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressCircle, 0, 0);
        try {
            colorCircle = parseColor(a, R.styleable.ProgressCircle_circleColor, colorCircle);
            colorText = parseColor(a, R.styleable.ProgressCircle_textColor, colorText);
            value = a.getInteger(R.styleable.ProgressCircle_value, value);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        // get background color (if exists)
        Drawable bg = this.getBackground();
        if (bg instanceof ColorDrawable)
            colorBackground = ((ColorDrawable) bg).getColor();

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(colorText);

        paintOuterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuterCircle.setColor(colorCircle);
        paintOuterCircle.setStyle(Paint.Style.FILL);

        paintInnerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInnerCircle.setColor(colorBackground);
        paintInnerCircle.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.waitingMode = false;
        if (this.animatorWaiting != null) {
            this.animatorWaiting.cancel();
            this.animatorWaiting = null;
        }

        this.value = value;
        computeTextPosition();

        this.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        radiusOuter = (Math.min(w, h) >> 1) * factorOuter;
        radiusInner = radiusOuter * factorInner;
        radiusText = radiusInner * factorText;

        float posxCenter = w >> 1;
        float posyCenter = h >> 1;
        posCenter = new PointF(posxCenter, posyCenter);

        rectOuterCircle = new RectF(
                posxCenter - radiusOuter,
                posyCenter - radiusOuter,
                posxCenter + radiusOuter,
                posyCenter + radiusOuter);
        rectOuterCircle_extended = new RectF(
                rectOuterCircle.left - 1,
                rectOuterCircle.top - 1,
                rectOuterCircle.right + 1,
                rectOuterCircle.bottom + 1);
        rectInnerCircle = new RectF(
                posxCenter - radiusInner,
                posyCenter - radiusInner,
                posxCenter + radiusInner,
                posyCenter + radiusInner);

        // compute the desired font height
        float const_height = 1000.0f;
        float width_over_height = computeValueTextWidth(const_height, 100) / const_height;
        sizeText = (float) Math.sqrt(4 * radiusText * radiusText / (1 + width_over_height * width_over_height));

        computeTextPosition();
    }

    public void setTypeface(Typeface ttf) {
        paintText.setTypeface(ttf);
    }

    private Rect getStringRect(String str) {
        Rect rect = new Rect();
        paintText.getTextBounds(str, 0, str.length(), rect);
        return rect;
    }

    private float computeValueTextWidth() {
        return computeValueTextWidth(sizeText, value);
    }

    private float computeValueTextWidth(float height, int value) {
        paintText.setTextSize(height);
        float widthNumber = getStringRect(Integer.toString(value)).right;
        paintText.setTextSize(height * factorPercent);
        float widthPercent = getStringRect("%").right;
        return widthNumber + widthPercent;
    }

    private void computeTextPosition() {
        String strNumber = Integer.toString(value);
        String strPercent = "%";

        paintText.setTextSize(sizeText);
        Rect rectNumber = getStringRect(strNumber);
        paintText.setTextSize(sizeText * factorPercent);
        Rect rectPercent = getStringRect(strPercent);

        float widthTotal = rectNumber.right + rectPercent.right;
        float heightTotal = rectNumber.top;

        float startxNumber = posCenter.x - widthTotal / 2;
        float startyNumber = posCenter.y - heightTotal / 2;

        float startxPercent = startxNumber + rectNumber.right;
        float startyPercent = startyNumber + heightTotal - rectPercent.top;

        posText_Value = new PointF(startxNumber, startyNumber);
        posText_Percent = new PointF(startxPercent, startyPercent);
    }

    private void drawValueText(Canvas canvas) {
        String strNumber = Integer.toString(value);
        String strPercent = "%";

        paintText.setTextSize(sizeText);
        canvas.drawText(strNumber, posText_Value.x, posText_Value.y, paintText);
        paintText.setTextSize(sizeText * factorPercent);
        canvas.drawText(strPercent, posText_Percent.x, posText_Percent.y, paintText);
    }

    private void drawProgressCircle(Canvas canvas) {
        canvas.rotate(-90, posCenter.x, posCenter.y);
        canvas.drawCircle(posCenter.x, posCenter.y, radiusOuter, paintOuterCircle);
        canvas.drawCircle(posCenter.x, posCenter.y, radiusInner, paintInnerCircle);
        canvas.drawArc(rectOuterCircle_extended, 0, -3.6f * (100.0f - value), true, paintInnerCircle);
        canvas.rotate(90, posCenter.x, posCenter.y);
    }

    private void drawWaitingCircle(Canvas canvas) {
        canvas.rotate(-90, posCenter.x, posCenter.y);
        canvas.drawCircle(posCenter.x, posCenter.y, radiusOuter, paintOuterCircle);
        canvas.drawCircle(posCenter.x, posCenter.y, radiusInner, paintInnerCircle);
        canvas.drawArc(rectOuterCircle_extended, (Float) animatorWaiting.getAnimatedValue(), -360f + arcWaiting, true, paintInnerCircle);
        canvas.rotate(90, posCenter.x, posCenter.y);
    }

    public void setWaiting() {
        animatorWaiting = ValueAnimator.ofFloat(0.0f, 360.0f);
        animatorWaiting.setInterpolator(new LinearInterpolator());
        animatorWaiting.setRepeatCount(ValueAnimator.INFINITE);
        animatorWaiting.setDuration(durationWaiting);
        animatorWaiting.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressCircleView.this.invalidate();
            }
        });

        this.waitingMode = true;
        animatorWaiting.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.waitingMode) {
            drawWaitingCircle(canvas);
        } else {
            drawProgressCircle(canvas);
            drawValueText(canvas);
        }
    }
}
