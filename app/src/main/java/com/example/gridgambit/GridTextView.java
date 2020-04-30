package com.example.gridgambit;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import static java.lang.Integer.parseInt;

public class GridTextView extends AppCompatTextView {

    int xLocation = 0;
    int yLocation = 0;
    int matches = 0;
    int firstMatch = 0;
    int secondMatch = 0;
    int thirdMatch = 0;

    GridTextView firstMatchObject;
    GridTextView secondMatchObject;
    GridTextView thirdMatchObject;


    public GridTextView(Context context) {
        super(context);
    }

    public GridTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    public int getValue() {
        return parseInt(this.getText().toString());
    }

}