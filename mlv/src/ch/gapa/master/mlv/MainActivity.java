package ch.gapa.master.mlv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import ch.gapa.master.mlv.view.GraphView;
import ch.gapa.master.mlv.view.SelectView;
import de.umass.lastfm.Period;

public class MainActivity extends Activity {

	View selectView;
	View currentView;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView( new GraphView(this));

		// Caller.getInstance ().setCache ( new MemoryCache () );
		//
		// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder
		// ().permitAll ().build ();
		// StrictMode.setThreadPolicy ( policy );
		// String user = "jystha";
		// Collection<Artist> chart = User.getTopArtists ( user, API_KEY );
		// for ( Artist artist : chart ) {
		// Log.v ( "Artist", artist.getName () );
		// }

		// setContentView(new TouchExampleView(this));
		selectView = new SelectView(this);
		setCurrentView(selectView);
	}

	public void setCurrentView(View v) {
		setContentView(v);
		currentView = v;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentView != selectView)
				setCurrentView(selectView);
			else
				finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	public void setContentGraphView(String user, Period period) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				(findViewById(R.id.editText1)).getApplicationWindowToken(), 0);
		ProgressDialog pd = ProgressDialog.show(this, "Graph creation", "Loading");
		setCurrentView(new GraphView(this, user, period));
		pd.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
