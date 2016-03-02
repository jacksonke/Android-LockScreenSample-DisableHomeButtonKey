package com.github.jacksonke.lockscreensample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jacksonke on 2016/2/1.
 */
public class GifView extends View {
    private Movie mMovie;
    private long mMovieStart = 0;

    //Set to false to use decodeByteArray
    private static final boolean DECODE_STREAM = true;

    private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        extraInit(context);
    }

    private void extraInit(Context context){
        setFocusable(true);

        java.io.InputStream is;

        try {
            is = context.getAssets().open("start_up.gif");

            if (DECODE_STREAM) {
                mMovie = Movie.decodeStream(is);
            } else {
                byte[] array = streamToBytes(is);
                mMovie = Movie.decodeByteArray(array, 0, array.length);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public GifView(Context context) {
        super(context);
        extraInit(context);
    }

    public void play(){
        mMovieStart = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(0xFF91CB58);

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }

        if (mMovie != null) {

            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            // play once
            int relTime = (int)((now - mMovieStart) /*% dur*/);
            if (relTime <= dur){
                mMovie.setTime(relTime);
            }
            else{
                mMovie.setTime(dur);
            }


            mMovie.draw(canvas, (getWidth() - mMovie.width()) / 2,
                    (getHeight() - mMovie.height()) / 2);

            invalidate();
        }
    }
}
