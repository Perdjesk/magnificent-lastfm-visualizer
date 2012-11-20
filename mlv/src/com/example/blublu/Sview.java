package com.example.blublu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Sview extends SurfaceView implements SurfaceHolder.Callback {

	SurfaceHolder surfaceHolder; // needed fro drawing
	SThread myThread; // will make MyThread next
	GestureDetector actionDetector;
	ScaleGestureDetector scaleGestureDetector;

	public Sview(Context context) {
		super(context);
		init(context);
	}

	public Sview(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init(context);
	}

	public void init(Context context) {
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		myThread = new SThread(surfaceHolder,this);
		actionDetector = new GestureDetector(context, new SimpleGestureListener(
				myThread));
		scaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener(myThread));
		setFocusable(true);
	}
	
	public boolean isScaleInProgress(){
		return scaleGestureDetector.isInProgress();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//Log.v("BLU", ev.toString());
		actionDetector.onTouchEvent(ev);
		scaleGestureDetector.onTouchEvent(ev);
		return true;

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	public void surfaceCreated(SurfaceHolder holder) {
		myThread.start();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
