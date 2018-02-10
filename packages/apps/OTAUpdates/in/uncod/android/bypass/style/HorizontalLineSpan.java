package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;

public class HorizontalLineSpan extends ReplacementSpan {
    private int mLineHeight;
    private Paint mPaint = new Paint();
    private int mTopBottomPadding;

    public HorizontalLineSpan(int color, int lineHeight, int topBottomPadding) {
        this.mPaint.setColor(color);
        this.mLineHeight = lineHeight;
        this.mTopBottomPadding = topBottomPadding;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        if (fm != null) {
            fm.ascent = (-this.mLineHeight) - this.mTopBottomPadding;
            fm.descent = 0;
            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return Integer.MAX_VALUE;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int middle = (top + bottom) / 2;
        int halfLineHeight = this.mLineHeight / 2;
        canvas.drawRect(x, (float) (middle - halfLineHeight), 2.14748365E9f, (float) (middle + halfLineHeight), this.mPaint);
    }
}
