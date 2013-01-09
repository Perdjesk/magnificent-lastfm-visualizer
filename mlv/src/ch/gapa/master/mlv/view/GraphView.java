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
import ch.gapa.master.mlv.model.GraphManager;
import ch.gapa.master.mlv.view.worker.GraphWorker;
import de.umass.lastfm.Period;
import ch.gapa.master.mlv.model.Constants;
public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

  SurfaceHolder surfaceHolder; // needed fro drawing
  GraphWorker worker; // will make MyThread next
  GestureDetector actionDetector;
  ScaleGestureDetector scaleGestureDetector;

  
  public GraphView ( Context context, String user, Period period ) {
    super( context );
    init( context,  user,  period );
  }

  public GraphView ( Context context, AttributeSet attributeSet ) {
    super( context, attributeSet );
    init( context,  Constants.DEFAULT_USER, Constants.DEFAULT_PERIOD);
  }

  public void init ( Context context, String user, Period period ) {
    surfaceHolder = getHolder();
    surfaceHolder.addCallback( this );
    worker = new GraphWorker( surfaceHolder, new GraphManager( user, period ) );
    actionDetector = new GestureDetector( context, new SimpleGestureListener( worker ) );
    scaleGestureDetector = new ScaleGestureDetector( context, new ScaleListener( worker ) );
    setFocusable( true );
  }

  public boolean isScaleInProgress () {
    return scaleGestureDetector.isInProgress();
  }

  @Override
  public boolean onTouchEvent ( MotionEvent ev ) {
    actionDetector.onTouchEvent( ev );
    scaleGestureDetector.onTouchEvent( ev );
    return true;
  }

  public void surfaceChanged ( SurfaceHolder holder, int format, int width, int height ) {
  }

  public void surfaceCreated ( SurfaceHolder holder ) {
		if (!worker.isAlive())
			worker.start();
  }

  public void surfaceDestroyed ( SurfaceHolder holder ) {
  }
}
