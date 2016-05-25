package com.cm.weatherview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cmad on 2016/5/24.
 */
public class WeatherView extends View {

    private static final String DATE_FORMAT = "HH:mm";

    private Paint mPaint;
    private String mStartTime;
    private String mEndTime;
    private String mCurrentTime;

    private float mTimeTextSize;
    private float mArcDashWidth;
    private float mArcDashGapWidth;
    private float mArcDashHeight;
    private float mArcRadius;
    private float mDefaultWeatherIconSize;
    private float mTextPadding;


    private int mArcColor;
    private int mArcSolidColor;
    private int mBottomLineColor;
    private int mTimeTextColor;
    private int mSunColor;

    private float mBottomLineHeight;
    private float mArcVerticalOffset;

    private Drawable mWeatherDrawable;

    private SimpleDateFormat mDateFormat;


    public WeatherView(Context context) {
        super(context);
        init();
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherViewStyle);
        mStartTime = attrArray.getString(R.styleable.WeatherViewStyle_startTime);
        mEndTime = attrArray.getString(R.styleable.WeatherViewStyle_endTime);
        mCurrentTime = attrArray.getString(R.styleable.WeatherViewStyle_currentTime);
        mTimeTextSize = attrArray.getDimension(R.styleable.WeatherViewStyle_timeTextSize, getResources().getDimension(R.dimen.default_text_size));
        mTimeTextColor = attrArray.getColor(R.styleable.WeatherViewStyle_timeTextColor, getResources().getColor(R.color.default_text_color));
        mWeatherDrawable = attrArray.getDrawable(R.styleable.WeatherViewStyle_weatherDrawable);
        mBottomLineHeight = attrArray.getDimension(R.styleable.WeatherViewStyle_bottomLineHeight, getResources().getDimension(R.dimen.default_bottom_line_height));
        mBottomLineColor = attrArray.getColor(R.styleable.WeatherViewStyle_bottomLineColor, getResources().getColor(R.color.default_bottom_line_color));
        mArcColor = attrArray.getColor(R.styleable.WeatherViewStyle_arcColor, getResources().getColor(R.color.default_arc_color));
        mArcSolidColor = attrArray.getColor(R.styleable.WeatherViewStyle_arcSolidColor, getResources().getColor(R.color.default_arc_solid_color));
        mArcDashWidth = attrArray.getDimension(R.styleable.WeatherViewStyle_arcDashWidth, getResources().getDimension(R.dimen.default_arc_dash_width));
        mArcDashGapWidth = attrArray.getDimension(R.styleable.WeatherViewStyle_arcDashGapWidth, getResources().getDimension(R.dimen.default_arc_dash_gap_width));
        mArcDashHeight = attrArray.getDimension(R.styleable.WeatherViewStyle_arcDashHeight, getResources().getDimension(R.dimen.default_arc_dash_height));
        mArcRadius = attrArray.getDimension(R.styleable.WeatherViewStyle_arcRadius, 0);
        mArcVerticalOffset = attrArray.getDimension(R.styleable.WeatherViewStyle_arcVerticalOffset, 0);
        mSunColor = attrArray.getColor(R.styleable.WeatherViewStyle_sunColor, getResources().getColor(R.color.default_sun_color));
        mTextPadding = attrArray.getDimension(R.styleable.WeatherViewStyle_textPadding, 0);
        attrArray.recycle();

        mDefaultWeatherIconSize = getResources().getDimension(R.dimen.default_weather_icon_size);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        setDefaultTime();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);


        mTimeTextSize = getResources().getDimension(R.dimen.default_text_size);
        mTimeTextColor = getResources().getColor(R.color.default_text_color);

        mBottomLineHeight = getResources().getDimension(R.dimen.default_bottom_line_height);
        mBottomLineColor = getResources().getColor(R.color.default_bottom_line_color);
        mArcColor = getResources().getColor(R.color.default_arc_color);
        mArcSolidColor = getResources().getColor(R.color.default_arc_solid_color);
        mArcDashWidth = getResources().getDimension(R.dimen.default_arc_dash_width);
        mArcDashGapWidth = getResources().getDimension(R.dimen.default_arc_dash_gap_width);
        mArcDashHeight = getResources().getDimension(R.dimen.default_arc_dash_height);
        mDefaultWeatherIconSize = getResources().getDimension(R.dimen.default_weather_icon_size);
        mSunColor = getResources().getColor(R.color.default_sun_color);
        setDefaultTime();
    }

    private void setDefaultTime() {

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);

        if (TextUtils.isEmpty(mStartTime)) {
            mStartTime = getResources().getString(R.string.default_start_time);
        }

        if (TextUtils.isEmpty(mEndTime)) {
            mEndTime = getResources().getString(R.string.default_end_time);
        }

        if (TextUtils.isEmpty(mCurrentTime)) {

            mCurrentTime = mDateFormat.format(new Date());
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            if (mArcRadius == 0) {
                setMeasuredDimension(width, height);
            } else {
                width = (int) (mArcRadius * 2 + getWidthGap());
                height = (int) (mArcRadius + getHeightGap());
                setMeasuredDimension(width, height);
            }

        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (mArcRadius == 0) {
                width = (height - getHeightGap()) * 2 + getWidthGap();
            } else {
                width = (int) (mArcRadius * 2 + getWidthGap());
            }
            setMeasuredDimension(width, height);

        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (mArcRadius == 0) {
                height = (width - getWidthGap())/2 + getHeightGap();
            } else {
                height = (int) (mArcRadius + getHeightGap());
            }

            setMeasuredDimension(width, height);

        } else {
            setMeasuredDimension(width, height);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        drawLine(canvas);
        drawArc(canvas);

    }

    /**
     * 画圆弧
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        getRadius();


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mArcColor);
        mPaint.setStrokeWidth(mArcDashHeight);

        float left = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight() - 2 * mArcRadius) / 2;
        float top = getHeight() - mArcRadius -getBottomHeightGap();
        float right = left + 2 * mArcRadius;
        float bottom = top + 2 * mArcRadius;
        RectF rectF = new RectF(left, top, right, bottom);
        DashPathEffect effect = new DashPathEffect(new float[]{mArcDashWidth, mArcDashGapWidth, mArcDashWidth, mArcDashGapWidth}, 0);
        mPaint.setPathEffect(effect);
        canvas.drawArc(rectF, 180, 180, false, mPaint);


        drawSolidArc(canvas, (int) mArcRadius, (int) left, rectF);


    }

    private void getRadius() {
        if (mArcRadius == 0) {

            int width = getWidth() -getWidthGap();
            int height = getHeight() - getHeightGap();

           if(width / 2 > height){
               mArcRadius = height;
           }else{
               mArcRadius = width / 2 ;
           }
        }
    }

    /**
     * 绘制天气图标
     *
     * @param canvas
     * @param point
     */
    private void drawWeatherDrawable(Canvas canvas, PointF point) {

        if (mWeatherDrawable != null) {
            int dw = mWeatherDrawable.getIntrinsicWidth() == 0 ? (int) mDefaultWeatherIconSize : mWeatherDrawable.getIntrinsicWidth();
            int dh = mWeatherDrawable.getIntrinsicHeight() == 0 ? (int) mDefaultWeatherIconSize : mWeatherDrawable.getIntrinsicHeight();

            Rect rect = new Rect();
            rect.left = (int) (point.x - dw / 2);
            rect.top = (int) (point.y - dh / 2);
            rect.right = rect.left + dw;
            rect.bottom = rect.top + dh;

            mWeatherDrawable.setBounds(rect);
            mWeatherDrawable.draw(canvas);
        } else {
            drawSun(canvas, point);
        }

    }

    /**
     * 画太阳
     *
     * @param canvas
     * @param point
     */
    private void drawSun(Canvas canvas, PointF point) {
        mPaint.setColor(mSunColor);

        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(point.x, point.y, mDefaultWeatherIconSize / 2, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDefaultWeatherIconSize / 3);


        float c = (float) (2 * mDefaultWeatherIconSize * Math.PI);

        int w = 3;
        float gapW = (c - 12 * 5) / 12;

        DashPathEffect effect = new DashPathEffect(new float[]{w, gapW, w, gapW}, 0);
        mPaint.setPathEffect(effect);

        canvas.drawCircle(point.x, point.y, mDefaultWeatherIconSize, mPaint);
    }

    /**
     * 画实心圆弧
     *
     * @param canvas
     * @param r
     * @param left
     * @param rectF
     */
    private void drawSolidArc(Canvas canvas, int r, int left, RectF rectF) {

        int angle = 0;
        try {
            long start = mDateFormat.parse(mStartTime).getTime();
            long end = mDateFormat.parse(mEndTime).getTime();
            long current = mDateFormat.parse(mCurrentTime).getTime();
            angle = (int) (1.0f * (current - start) / (end - start) * 180);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mArcSolidColor);
        canvas.drawArc(rectF, 180, angle, false, mPaint);

        PointF point = calcArcEndPointXY(rectF.centerX(), rectF.centerY(), r, 180 + angle);

        drawTriangle(canvas, rectF, point);
        drawWeatherDrawable(canvas, point);

        drawText(canvas, rectF);
    }

    private void drawText(Canvas canvas, RectF rect) {

        mPaint.setColor(mTimeTextColor);
        mPaint.setTextSize(mTimeTextSize);

        int startTextWidth = getTextWidth(mPaint, mStartTime);
        int endTextWidth = getTextWidth(mPaint, mEndTime);

        int textHeight = getTextHeight();

        mPaint.setPathEffect(null);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawText(mStartTime, rect.left - startTextWidth / 2, rect.centerY() + textHeight + mTextPadding, mPaint);
        canvas.drawText(mEndTime, rect.right - endTextWidth / 2 - 2, rect.centerY() + textHeight + mTextPadding, mPaint);
    }

    private int getTextHeight() {
        mPaint.setTextSize(mTimeTextSize);
        Paint.FontMetrics fm = mPaint.getFontMetrics();// 得到系统默认字体属性
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 画三角形
     *
     * @param canvas
     * @param rect
     * @param point
     */
    private void drawTriangle(Canvas canvas, RectF rect, PointF point) {
        Path path = new Path();
        path.moveTo(rect.left, rect.centerY());// 此点为多边形的起点
        path.lineTo(point.x, point.y);
        path.lineTo(point.x, rect.centerY());
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, mPaint);
    }

    /**
     * 画底部线条
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {

        mPaint.setColor(mBottomLineColor);
        mPaint.setStrokeWidth(mBottomLineHeight);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawLine(getPaddingLeft(), getHeight() - getBottomHeightGap(), getWidth() - getPaddingRight(), getHeight() -getBottomHeightGap(), mPaint);

    }


    private int getWidthGap(){
        return getPaddingLeft() + getPaddingRight() + getTextWidth(mStartTime)/2+getTextWidth(mEndTime)/2;
    }

    private int getHeightGap(){
        return (int) (getPaddingTop() + getPaddingBottom() + mTextPadding + mBottomLineHeight + getWeatherHeight()/2) + getTextHeight();
    }

    private int getBottomHeightGap(){
        return (int) (getPaddingBottom() + getTextHeight() + mTextPadding);
    }

    private int getWeatherHeight(){
        if(mWeatherDrawable == null){
            return (int) mDefaultWeatherIconSize*2;
        }
        if(mWeatherDrawable.getIntrinsicHeight() == 0){
            return (int) mDefaultWeatherIconSize*2;
        }
        return mWeatherDrawable.getIntrinsicHeight();
    }

    private int getWeatherWidth(){
        if(mWeatherDrawable == null){
            return (int) mDefaultWeatherIconSize*2;
        }
        if(mWeatherDrawable.getIntrinsicWidth() == 0){
            return (int) mDefaultWeatherIconSize*2;
        }
        return mWeatherDrawable.getIntrinsicWidth();
    }




    //依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
    public PointF calcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle) {

        PointF point = new PointF();

        //将角度转换为弧度
        float arcAngle = (float) (Math.PI * cirAngle / 180.0);

        //当angle = 90°时，radian = ∏ / 2 = ∏ * 90°/ 180°= ∏ * angle / 180°，
        //当angle = 180°时，radian = ∏ = ∏ * 180°/ 180°= ∏ * angle / 180°，
        //所以radian(弧度) = ∏ * angle / 180（1弧度是等于半径的圆弧对应的圆心角，1度是1/360圆心角）

        if (cirAngle < 90)     //直角的三角形斜边是半径
        {
            point.x = cirX + (float) (Math.cos(arcAngle)) * radius;
            point.y = cirY + (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 90) {
            point.x = cirX;
            point.y = cirY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
            point.x = cirX - (float) (Math.cos(arcAngle)) * radius;
            point.y = cirY + (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 180) {
            point.x = cirX - radius;
            point.y = cirY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
            point.x = cirX - (float) (Math.cos(arcAngle)) * radius;
            point.y = cirY - (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 270) {
            point.x = cirX;
            point.y = cirY - radius;
        } else {
            arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
            point.x = cirX + (float) (Math.cos(arcAngle)) * radius;
            point.y = cirY - (float) (Math.sin(arcAngle)) * radius;
        }

        return point;
    }


    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public int getTextWidth(String str) {
        mPaint.setTextSize(mTimeTextSize);
        return getTextWidth(mPaint, str);
    }

}
