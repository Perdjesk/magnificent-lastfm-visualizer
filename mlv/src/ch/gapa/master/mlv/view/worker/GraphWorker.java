package ch.gapa.master.mlv.view.worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.SurfaceHolder;
import ch.gapa.master.mlv.data.Edge;
import ch.gapa.master.mlv.data.Graph;
import ch.gapa.master.mlv.model.ArtistWrapper;
import ch.gapa.master.mlv.view.GraphView;

public class GraphWorker extends Thread {

	SurfaceHolder surfaceHodler;

	Canvas canvas;
	Paint paintNode = new Paint();
	Paint paintTextFps = new Paint();

	float scaleFactor = 1.0f;
	float dx, dy;
	float focusX, focusY;
	GraphView view;

	int cwidth, cheight;
	Point tapPosition = new Point(0,0);

	int fps = 0;
	int framecount = 0;
	long prevtime, deltaTime, time;
	Bitmap bm;
	
	Random rand = new Random();
	
	Graph<Integer> fullyConnectedGraph;
	
	List<ArtistWrapper> listArtist = new ArrayList<ArtistWrapper>();
	ArtistWrapper lastHit = new ArtistWrapper();

	// Public methods section
	
	public GraphWorker(SurfaceHolder surfaceHodler, GraphView view) {
		this.surfaceHodler = surfaceHodler;
		paintNode.setColor(Color.YELLOW);
		paintTextFps.setColor(Color.CYAN);
	}

	@Override
	public void run() {
		updateData();
		while (true) {
			updateScreen();
			fps();
		}
	}
	
	
	public void setScaleFactor(float sc, float focusx, float focusy) {
		this.scaleFactor *= sc;
		this.scaleFactor = Math.max(0.001f, Math.min(this.scaleFactor, 100.0f));
		this.focusX = focusx;
		this.focusY = focusy;
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



	private void updateData() {		
		for ( int i = 0; i < 100; i++ ) {
			listArtist.add(new ArtistWrapper(new Point(rand.nextInt(1000),rand.nextInt(1000))));
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

		canvas.scale(scaleFactor, scaleFactor, (cwidth) / 2, (cheight) / 2);
		canvas.translate(-dx, -dy);

		hitDetection(canvas);

		Iterator<ArtistWrapper> it = listArtist.iterator();
		while (it.hasNext()) {
			it.next().draw(canvas);
		}
		canvas.restore();

		surfaceHodler.unlockCanvasAndPost(canvas);
	}

	private void hitDetection(Canvas canvas) {
		// To avoid a boolean we assume that when tap point is at 0,0 no tap
		// event occurred
		if (tapPosition.equals(0, 0))
			return;

		lastHit.setSelected(false);
		
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

		Iterator<ArtistWrapper> it = listArtist.iterator();
		ArtistWrapper shape;
		while (it.hasNext()) {
			shape = it.next();
			if (shape.contains(tapLocation)) {
				shape.setSelected(true);
				lastHit = shape;
			}
		}
		// We set tapPosition to default when event is handled
		tapPosition.set(0, 0);
	}
}
