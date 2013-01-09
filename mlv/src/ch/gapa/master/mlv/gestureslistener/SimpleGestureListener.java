package ch.gapa.master.mlv.gestureslistener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import ch.gapa.master.mlv.view.worker.GraphWorker;

public class SimpleGestureListener extends
		GestureDetector.SimpleOnGestureListener {
	GraphWorker worker;

	public SimpleGestureListener(GraphWorker worker) {
		this.worker = worker;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		worker.onLongPress(e.getX(), e.getY());
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		worker.onDoubleTap(e.getX(), e.getY());
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		worker.onSingleTap(e.getX(), e.getY());
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		worker.setTranslates(distanceX, distanceY);
		return true;
	}
}
