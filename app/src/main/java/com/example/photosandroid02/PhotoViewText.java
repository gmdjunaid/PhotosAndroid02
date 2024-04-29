package com.example.photosandroid02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PhotoViewText extends androidx.appcompat.widget.AppCompatTextView {
    public PhotoViewText(Context context) {
        super(context);
        init();
    }

    public PhotoViewText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoViewText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set custom attributes if needed
        this.setTypeface(this.getTypeface(), Typeface.BOLD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Additional custom drawing if necessary
    }
}
