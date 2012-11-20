package com.example.blublu;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
	SThread worker;
	
	public SimpleGestureListener(SThread worker){
		this.worker=worker;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.v("BADBLU", e.toString());
		
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.v("SCROOOOOL", distanceX+"/"+distanceY);
		worker.setTranslates(distanceX, distanceY);
		return true;
	}
}


