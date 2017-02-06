package com.example.administrator.appmanager_frg_vp_demo1.mView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class CircleView extends View {
    int width ;
    int height;
    int precent;
    Bitmap mBitmap ;
    int viewColor = 0xff00ff00;


    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // �Ȼ��һ�� bitmap��  ��
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        mBitmap = Bitmap.createBitmap(width, height,
                Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        Paint mPaint = new Paint();
        mPaint.setColor(viewColor);
        mPaint.setAntiAlias(true);
        mCanvas.drawCircle(width / 2, height / 2, width / 2, mPaint);

        canvas.drawBitmap(mBitmap, new Rect(0, height * (100 - precent) / 100, width, height) ,
                new Rect(0, height * (100 - precent) / 100, width, height), null);
    }

    public void setPrecent(int precent) {
        this.precent = precent;
		/*if (precent >= 90){
			viewColor = .....
		}else if (precent >= 80){
			viewColor = .....
		}else if (precent >= 70){
			viewColor = .....

		}
		.precent........
		else{

		}*/
        invalidate();
    }
}
