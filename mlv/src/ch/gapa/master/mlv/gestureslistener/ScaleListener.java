package ch.gapa.master.mlv.gestureslistener;

import android.view.ScaleGestureDetector;
import ch.gapa.master.mlv.view.worker.GraphWorker;

public class ScaleListener extends
		ScaleGestureDetector.SimpleOnScaleGestureListener {
	GraphWorker worker;

	
	long timeLastBegin =0;
	public ScaleListener(GraphWorker worker) {
		this.worker = worker;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (detector.isInProgress())
			worker.setScaleFactor(detector.getScaleFactor(),
					detector.getFocusX(), detector.getFocusY());
		return true;
	}
	
	@Override
	 public boolean onScaleBegin(ScaleGestureDetector detector){
		timeLastBegin = detector.getEventTime();
		return true;		 
	 }
	
	@Override
	 public void onScaleEnd(ScaleGestureDetector detector){
		float dist = Math.abs(detector.getPreviousSpan() - detector.getCurrentSpan());
		long time = detector.getEventTime() - timeLastBegin;
		if (dist <2 && time<200)
			worker.onTwoFingerTap(); 
	 }
	

}
