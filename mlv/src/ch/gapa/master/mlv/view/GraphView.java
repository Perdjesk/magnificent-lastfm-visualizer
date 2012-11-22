package ch.gapa.master.mlv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import ch.gapa.master.mlv.gestureslistener.ScaleListener;
import ch.gapa.master.mlv.gestureslistener.SimpleGestureListener;
import ch.gapa.master.mlv.view.worker.GraphWorker;

public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

	SurfaceHolder surfaceHolder; // needed fro drawing
	GraphWorker worker; // will make MyThread next
	GestureDetector actionDetector;
	ScaleGestureDetector scaleGestureDetector;

	public GraphView(Context context) {
		super(context);
		init(context);
	}

	public GraphView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init(context);
	}

	public void init(Context context) {
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		worker = new GraphWorker(surfaceHolder,this);
		actionDetector = new GestureDetector(context, new SimpleGestureListener(
				worker));
		scaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener(worker));
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
		worker.start();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
