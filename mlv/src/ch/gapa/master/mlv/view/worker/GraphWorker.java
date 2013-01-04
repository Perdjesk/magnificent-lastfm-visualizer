package ch.gapa.master.mlv.view.worker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import ch.gapa.master.mlv.data.BusProvider;
import ch.gapa.master.mlv.model.GraphManager;
import ch.gapa.master.mlv.view.TapEvent;

public class GraphWorker extends Thread {

	private final SurfaceHolder surfaceHodler;

	private final GraphManager _manager;

	private Canvas canvas;

	private final Paint paintTextFps = new Paint();
	
	private final Paint paintDoButton = new Paint();

	private float scaleFactor = 1.0f;

	private float dx, dy;

	private int cwidth, cheight;

	private Point tapPosition = new Point(0, 0);

	private int fps = 0;

	private int framecount = 0;

	private long prevtime, deltaTime, time;
	Rect undoButton, redoButton;

	// Public methods section

	public GraphWorker(final SurfaceHolder surfaceHodler,
			final GraphManager manager) {
		this.surfaceHodler = surfaceHodler;
		paintTextFps.setColor(Color.CYAN);
		paintDoButton.setColor(Color.DKGRAY);
		this._manager = manager;
	}

	@Override
	public void run() {
		while (true) {
			updateScreen();
			fps();
		}
	}

	public void setScaleFactor(float sc, float focusx, float focusy) {
		this.scaleFactor *= sc;
		this.scaleFactor = Math.max(0.001f, Math.min(this.scaleFactor, 100.0f));
	}

	public void setTranslates(float dx, float dy) {
		this.dx += dx / scaleFactor;
		this.dy += dy / scaleFactor;
	}

	public void onSingleTap(float x, float y) {
		tapPosition.set((int) x, (int) y);
	}

	// Private methods section

	private void fps() {
		framecount++;
		time = System.currentTimeMillis();
		deltaTime = time - prevtime;
		if (deltaTime > 500) {
			fps = (int) Math.round((framecount / ((double) deltaTime / 1000)));
			framecount = 0;
			prevtime = time;
		}
	}

	private void updateScreen() {
		canvas = surfaceHodler.lockCanvas();

		if (canvas == null)
			return;

		cwidth = canvas.getWidth();
		cheight = canvas.getHeight();

		canvas.save();

		canvas.drawColor(Color.BLACK);

		canvas.drawText("FPS: " + fps, 10, 10, paintTextFps);
		undoButton = new Rect(0, cheight - 50, 50, cheight);
		redoButton = new Rect(cwidth - 50, cheight - 50, cwidth, cheight);
		canvas.drawRect(undoButton, paintDoButton);
		canvas.drawText("UNDO", 10, cheight-25, paintTextFps);
		canvas.drawRect(redoButton, paintDoButton);
		canvas.drawText("REDO", cwidth - 40, cheight-25, paintTextFps);

		canvas.scale(scaleFactor, scaleFactor, (cwidth) / 2, (cheight) / 2);
		canvas.translate(-dx, -dy);

		// TODO: Check, normalement peut être directement intégré dans
		// l'event (? lock unlock attention)
		hitDetection(canvas);

		_manager.draw(canvas);

		canvas.restore();

		surfaceHodler.unlockCanvasAndPost(canvas);
	}

	private void hitDetection(Canvas canvas) {
		// To avoid a boolean we assume that when tap point is at 0,0 no tap
		// event occurred
		if (tapPosition.equals(0, 0))
			return;

		if (redoButton.contains(tapPosition.x, tapPosition.y)) {
			_manager.redo();
			return;
		}
		if (undoButton.contains(tapPosition.x, tapPosition.y)) {
			_manager.undo();
			return;
		}

		// We get the inverse of the matrix transformation. By default the
		// matrix transformation map canvas coordinates to screen coordinates,
		// and we need to
		// do the inverse operation.
		Matrix matrixInverse = new Matrix();
		boolean invertible = canvas.getMatrix().invert(matrixInverse);

		// If the matrix is not invertible you are in trouble and hitDetection
		// can not continue.
		if (!invertible) {
			Log.e("MLV-hitDetection",
					"A matrix from canvas.getMatrix() is not invertible");
			return;
		}

		float[] pointTap = { tapPosition.x, tapPosition.y };
		matrixInverse.mapPoints(pointTap);
		Point tapLocation = new Point((int) pointTap[0], (int) pointTap[1]);


		// Generate Event
		BusProvider.INSTANCE.getBus().post(new TapEvent(tapLocation));

		// We set tapPosition to default when event is handled
		tapPosition.set(0, 0);
	}
}
