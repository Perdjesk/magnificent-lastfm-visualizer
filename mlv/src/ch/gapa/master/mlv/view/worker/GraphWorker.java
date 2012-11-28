package ch.gapa.master.mlv.view.worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.SurfaceHolder;
import ch.gapa.master.mlv.view.GraphView;

public class GraphWorker extends Thread {

	SurfaceHolder surfaceHodler;

	Canvas canvas;
	Paint paintNode = new Paint();
	Paint paintTextFps = new Paint();

	int cx = 10, cy = 10;
	float scaleFactor = 1.0f;
	float dx, dy, lsf;
	float focusX, focusY;
	GraphView view;

	int cwidth, cheight;

	int fps = 0;
	int framecount = 0;
	long prevtime, deltaTime, time;
	Bitmap bm;
	List<ShapeDrawable> listShapes = new ArrayList<ShapeDrawable>();
	int tapx,tapy;

	public GraphWorker(SurfaceHolder surfaceHodler, GraphView view) {
		this.surfaceHodler = surfaceHodler;
		// paintNode.setAntiAlias(true);
		paintNode.setColor(Color.YELLOW);
		paintTextFps.setColor(Color.CYAN);

		bm = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		c.drawCircle(5, 5, 5, paintNode);
	}

	@Override
	public void run() {
		updateData();
		while (true) {
			hitDetection();
			updateScreen();
			fps();
		}
	}

	public void fps() {
		framecount++;
		time = System.currentTimeMillis();
		deltaTime = time - prevtime;
		if (deltaTime > 500) {
			fps = (int) (framecount / ((double) deltaTime / 1000));
			framecount = 0;
			prevtime = time;
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
	
	public void onSingleTap(int x, int y){
		this.tapx=(int) (x+dx);
		this.tapy=(int) (y+dy);		
	}

	public void setTapLocation() {

	}

	private void updateData() {
		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 20; j++) {
				ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
				mDrawable.getPaint().setAntiAlias(false);
				mDrawable.getPaint().setColor(Color.MAGENTA);
				mDrawable.setBounds(i*20, j*20, i*20 + 10, j*20 + 10);
				listShapes.add(mDrawable);
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

		Iterator<ShapeDrawable> it = listShapes.iterator();
		while (it.hasNext()) {
			it.next().draw(canvas);
		}
		canvas.restore();

		surfaceHodler.unlockCanvasAndPost(canvas);
	}
	private void hitDetection(){
		if (tapx==0 && tapy==0) return;
		Log.v("tap",tapx+" / "+tapy);
		Iterator<ShapeDrawable> it = listShapes.iterator();
		while (it.hasNext()) {
			if(it.next().getBounds().contains(tapx, tapy)){
				Log.v("BLUBL","We have HIIIT");
			}
		}
		tapx=0; tapy=0;
	}
}
