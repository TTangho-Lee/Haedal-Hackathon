package com.example.parttimecalander.calander;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Span to draw a rectangle with text centered under a section of text
 */
public class RectangleSpan implements LineBackgroundSpan {

    private final float width;
    private final float height;
    private final int color;
    private final String text;

    /**
     * Create a span to draw a rectangle with text
     *
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     * @param color  Color of the rectangle
     * @param text   Text to display inside the rectangle
     */
    public RectangleSpan(float width, float height, int color, String text) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.text = text;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        // Save original paint color
        int oldColor = paint.getColor();
        Paint.Align oldAlign = paint.getTextAlign();

        // Draw rectangle
        if (color != 0) {
            paint.setColor(color);
        }
        float centerX = (left + right) / 2f;
        float rectTop = bottom + 10; // Distance below the text
        float rectBottom = rectTop + height;
        float rectLeft = centerX - (width / 2);
        float rectRight = centerX + (width / 2);
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

        // Draw text inside rectangle
        if (text != null) {
            paint.setColor(0xFFFFFFFF); // White color for text
            paint.setTextAlign(Paint.Align.CENTER);
            float textX = centerX;
            float textY = rectTop + (height / 2) - ((paint.descent() + paint.ascent()) / 2);
            canvas.drawText(text, textX, textY, paint);
        }

        // Restore original paint properties
        paint.setColor(oldColor);
        paint.setTextSize(12.0f);
        paint.setTextAlign(oldAlign);
    }
}
