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
import de.umass.lastfm.Caller;
import de.umass.lastfm.Period;
import de.umass.lastfm.cache.SQLiteCache;

public class MainActivity extends Activity {

	View selectView;
	View currentView;
	public static Activity mainActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity=this;
		Caller.getInstance ().setCache ( new SQLiteCache (this) );
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
		ProgressDialog pd = ProgressDialog.show(this, "Graph creation",
				"Loading");
		setCurrentView(new GraphView(this, user, period));
		pd.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
