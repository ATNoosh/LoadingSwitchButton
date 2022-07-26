package ir.atnoosh.loadingswitchbutton;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class LoadingSwitchButton extends View {

    public enum Statuses {
        ON,
        OFF,
        TURNING_ON,
        TURNING_OFF
    }

    private int animStep = 0;
    private int numberOfCircles;
    private double radiusRatio;
    private int strokeWidth = 10;
    private int radius;

    public class MyPoint {
        public MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double x;
        public double y;
    }

    private List<MyPoint> circleCenters;


    private Statuses status;
    private Paint mBgPaint;
    private Paint mFgPaint;
    private Paint mLoadingPaint;
    private ValueAnimator loadingAnimator;

    private Context mContext;

    Resources.Theme theme;
    TypedValue typedValueColorPrimary;
    TypedValue typedValueColorOnPrimary;
    TypedValue typedValueColorSecondary;
    TypedValue typedValueColorOnSecondary;
    TypedArray attributes;

    public LoadingSwitchButton(final Context context) {
        this(context, null);
    }

    public LoadingSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;

        theme = mContext.getTheme();
        typedValueColorPrimary = new TypedValue();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValueColorPrimary, true);
        typedValueColorOnPrimary = new TypedValue();
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValueColorOnPrimary, true);
        typedValueColorSecondary = new TypedValue();
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValueColorSecondary, true);
        typedValueColorOnSecondary = new TypedValue();
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValueColorOnSecondary, true);
        attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingSwitchButton, defStyleAttr, 0);

        strokeWidth = attributes.getDimensionPixelSize(R.styleable.LoadingSwitchButton_lsb_strokeWidth, 10);
        numberOfCircles = attributes.getInt(R.styleable.LoadingSwitchButton_lsb_numberOfCircles, 8);
        radiusRatio = attributes.getFloat(R.styleable.LoadingSwitchButton_lsb_radiusRatio, 1f / 6f);

        initAnimation();

        this.status = Statuses.OFF;
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);


        mFgPaint = new Paint();
        mFgPaint.setAntiAlias(true);


        mLoadingPaint = new Paint();
        mLoadingPaint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLoadingPaint.setColor(getContext().getColor(com.google.android.material.R.color.design_default_color_on_primary));
        }

    }

    @Override
    public void onDraw(Canvas canvas) {

        radius = (canvas.getWidth() - 2 * strokeWidth) / 4;
        RectF middleRectangle = new RectF(radius + strokeWidth, 0, 2 * (radius + strokeWidth), 2 * (radius + strokeWidth));

        switch (this.status) {
            case OFF:
                mBgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOff, typedValueColorSecondary.data));
                mFgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_foregroundColorOff, typedValueColorOnSecondary.data));
                canvas.drawRect(middleRectangle, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius, mFgPaint);
                break;
            case ON:
                mBgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOn, typedValueColorPrimary.data));
                mFgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_foregroundColorOn, typedValueColorOnPrimary.data));
                canvas.drawRect(middleRectangle, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius, mFgPaint);
                break;
            case TURNING_ON:
                mBgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOn, typedValueColorPrimary.data));
                mFgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_foregroundColorOn, typedValueColorOnPrimary.data));
                canvas.drawRect(middleRectangle, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius, mFgPaint);
                initCircles(new MyPoint(strokeWidth + 2 * radius, strokeWidth + radius));
                for (int i = 0; i < circleCenters.size(); i++) {
                    mLoadingPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOn, typedValueColorPrimary.data));
                    mLoadingPaint.setAlpha((animStep + i) * (256 / numberOfCircles));
                    canvas.drawCircle((float) circleCenters.get(i).x, (float) circleCenters.get(i).y, (float) (radiusRatio * radius), mLoadingPaint);
                }
                break;
            case TURNING_OFF:
                mBgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOff, typedValueColorSecondary.data));
                mFgPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_foregroundColorOff, typedValueColorOnSecondary.data));
                canvas.drawRect(middleRectangle, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + 2 * radius, strokeWidth + radius, radius + strokeWidth, mBgPaint);
                canvas.drawCircle(strokeWidth + radius, strokeWidth + radius, radius, mFgPaint);
                initCircles(new MyPoint(strokeWidth + radius, strokeWidth + radius));
                for (int i = 0; i < circleCenters.size(); i++) {
                    mLoadingPaint.setColor(attributes.getColor(R.styleable.LoadingSwitchButton_lsb_backgroundColorOn, typedValueColorPrimary.data));
                    mLoadingPaint.setAlpha((animStep + i) * (256 / numberOfCircles));
                    canvas.drawCircle((float) circleCenters.get(i).x, (float) circleCenters.get(i).y, (float) (radiusRatio * radius), mLoadingPaint);
                }
                break;

        }
    }

    private void initCircles(MyPoint centerPoint) {

        double modifiedRadius = radius * 0.9f;
        circleCenters = new ArrayList<>();

        numberOfCircles = numberOfCircles < 1 ? 8 : numberOfCircles;
        radiusRatio = (radiusRatio > 1 || radiusRatio <= 0) ? 1f / 6f : radiusRatio;
        double baseAngle = Math.toRadians(360f / numberOfCircles);
        for (int i = 0; i < numberOfCircles; i++) {
            circleCenters.add(new MyPoint(centerPoint.x + (1 - radiusRatio) * modifiedRadius * Math.cos(i * baseAngle), centerPoint.y + (1 - radiusRatio) * modifiedRadius * Math.sin(i * baseAngle)));
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else {
            result = 10;
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else {
            result = (int) (2 * strokeWidth + getWidth() * 2f / 3f);
        }
        return result ;
    }

    public void setBorderWidth(float width) {
        invalidate();
    }

    public float getBorderWidth() {
        return mBgPaint.getStrokeWidth();
    }

    public void setBorderColor(int color) {
        mBgPaint.setColor(color);
        invalidate();
    }

    public int getBorderColor() {
        return mBgPaint.getColor();
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public void toggleStatus(boolean isSuccessful) {
        switch (this.status) {
            case ON:
                this.status = Statuses.TURNING_OFF;
                break;
            case OFF:
                this.status = Statuses.TURNING_ON;
                break;
            case TURNING_ON:
                this.status = isSuccessful ? Statuses.ON : Statuses.OFF;
                break;
            case TURNING_OFF:
                this.status = isSuccessful ? Statuses.OFF : Statuses.ON;
                break;
        }

        invalidate();

    }


    private void initAnimation() {
        numberOfCircles = numberOfCircles < 1 ? 8 : numberOfCircles;
        int[] arr = new int[numberOfCircles];
        for (int i = 0; i < arr.length; i++)
            arr[i] = i + 1;
        loadingAnimator = ValueAnimator.ofInt(arr);
        loadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimator.setDuration(1000);
        loadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animStep = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        loadingAnimator.start();


    }

}