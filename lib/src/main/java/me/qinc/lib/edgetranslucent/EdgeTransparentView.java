/*
 * Copyright 2017. SHENQINCI(沈钦赐)<946736079@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.qinc.lib.edgetranslucent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 任意View边沿透明
 * Created by shenqinci on 2016/12/3.
 */
public class EdgeTransparentView extends FrameLayout {
    private Paint mPaint;
    private int position;
    private float drawSize;

    private int topMask = 0x01;
    private int bottomMask = topMask << 1;
    private int leftMask = topMask << 2;
    private int rightMask = topMask << 3;

    private int mWidth;
    private int mHeight;

    public EdgeTransparentView(Context context) {
        this(context, null);
    }

    public EdgeTransparentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EdgeTransparentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));


        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EdgeTransparentView);
        position = typedArray.getInt(R.styleable.EdgeTransparentView_edge_position, 0);
        drawSize = typedArray.getDimension(R.styleable.EdgeTransparentView_edge_width, Utils.d2p(getContext(), 20));
        typedArray.recycle();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initShader();
        mWidth = getWidth();
        mHeight = getHeight();
    }

    //渐变颜色
    private int[] mGradientColors = {0xffffffff, 0x00000000};
    //渐变位置
    private float[] mGradientPosition = new float[]{0, 1};

    private void initShader() {
        mPaint.setShader(new LinearGradient(0, 0, 0, drawSize, mGradientColors, mGradientPosition, Shader.TileMode.CLAMP));
    }

   @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int layerSave = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (position == 0 || (position & topMask) != 0) {
            canvas.drawRect(0, 0, mWidth, drawSize, mPaint);
        }

        if (position == 0 || (position & bottomMask) != 0) {
            int save = canvas.save();
            canvas.rotate(180, mWidth / 2f, mHeight / 2f);
            canvas.drawRect(0, 0, mWidth, drawSize, mPaint);
            canvas.restoreToCount(save);
        }

        float offset = (mHeight - mWidth) / 2f;
        if (position == 0 || (position & leftMask) != 0) {
            int saveCount = canvas.save();
            canvas.rotate(90, mWidth / 2f, mHeight / 2f);
            canvas.translate(0, offset);
            canvas.drawRect(0 - offset, 0, mWidth + offset, drawSize, mPaint);
            canvas.restoreToCount(saveCount);
        }

        if (position == 0 || (position & rightMask) != 0) {
            int saveCount = canvas.save();
            canvas.rotate(270, mWidth / 2f, mHeight / 2f);
            canvas.translate(0, offset);
            canvas.drawRect(0 - offset, 0, mWidth + offset, drawSize, mPaint);
            canvas.restoreToCount(saveCount);
        }

        canvas.restoreToCount(layerSave);
        return drawChild;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
