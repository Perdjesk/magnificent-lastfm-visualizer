package ch.gapa.master.mlv.view.worker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import ch.gapa.master.mlv.view.GraphView;

public class GraphWorker extends Thread {

	SurfaceHolder surfaceHodler;

	Canvas canvas;
	Paint paint = new Paint();
	int cx = 10, cy = 10;
	float scaleFactor = 1.0f;
	float dx, dy, lsf;
	float focusX, focusY;
	GraphView view;
	boolean drawed = false;
	int fps;
	int framecount;

	public GraphWorker(SurfaceHolder surfaceHodler, GraphView view) {
		this.surfaceHodler = surfaceHodler;
		paint.setAntiAlias(true);
	}

	@Override
	public void run() {
		while (true) {
			updateData();
			updateScreen();
		}
	}

	public void setScaleFactor(float sc, float focusx, float focusy) {
		this.scaleFactor *= sc;
		this.scaleFactor = Math.max(0.001f, Math.min(this.scaleFactor, 100.0f));
		this.focusX = focusx;
		this.focusY = focusy;
	}

	public void setTranslates(float dx, float dy) {
		// DO NOT DO THIS DIVISION IN UPDATESCREEN
		this.dx += dx / scaleFactor;
		this.dy += dy / scaleFactor;
	}
	
	public void setTapLocation( ) {
		
	}

	private void updateData() {
		// cx+=1;
		// cy+=1;
		// scaleFactor+=0.02f;
	}

	private void updateScreen() {
		paint.setColor(Color.BLACK);
		Paint pp = new Paint();
		pp.setColor(Color.GREEN);
		canvas = surfaceHodler.lockCanvas();
		if (canvas == null)
			return;
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		canvas.save();

		canvas.scale(scaleFactor, scaleFactor, (width) / 2, (height) / 2);
		canvas.translate(-dx, -dy);

		canvas.drawColor(Color.BLUE);
		for (float i = 10; i < canvas.getMaximumBitmapWidth() / 10; i += 50)
			for (float j = 10; j < canvas.getMaximumBitmapHeight() / 10; j += 50)
				canvas.drawCircle(i, j, 10, paint);

		canvas.drawCircle(width / 2, canvas.getMaximumBitmapHeight(), 500, pp);

		canvas.drawText("FPS: " + fps, 10, 35, paint);    
		canvas.restore();
		
		surfaceHodler.unlockCanvasAndPost(canvas);
	}

}
