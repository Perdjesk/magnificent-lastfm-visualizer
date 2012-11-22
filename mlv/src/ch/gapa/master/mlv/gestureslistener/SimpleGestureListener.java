package ch.gapa.master.mlv.gestureslistener;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import ch.gapa.master.mlv.view.worker.GraphWorker;

public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
	GraphWorker worker;
	
	public SimpleGestureListener(GraphWorker worker){
		this.worker=worker;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.v("DABLU",e.getX()+"/"+e.getY()+"&&"+e.getRawX()+"/"+e.getRawY());
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		worker.setTranslates(distanceX, distanceY);
		return true;
	}
}


