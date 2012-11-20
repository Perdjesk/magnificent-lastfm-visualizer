package com.example.blublu;

import android.util.Log;
import android.view.ScaleGestureDetector;

public class ScaleListener extends
		ScaleGestureDetector.SimpleOnScaleGestureListener {
	SThread worker;

	public ScaleListener(SThread worker) {
		this.worker = worker;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		Log.v("BLUDSAB", detector.getFocusX()+"/"+detector.getFocusY());
		if (detector.isInProgress())
			worker.setScaleFactor(detector.getScaleFactor(),
					detector.getFocusX(), detector.getFocusY());
		return true;
	}

}
