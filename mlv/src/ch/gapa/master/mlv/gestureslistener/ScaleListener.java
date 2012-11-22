package ch.gapa.master.mlv.gestureslistener;

import android.view.ScaleGestureDetector;
import ch.gapa.master.mlv.view.worker.GraphWorker;

public class ScaleListener extends
		ScaleGestureDetector.SimpleOnScaleGestureListener {
	GraphWorker worker;

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

}
