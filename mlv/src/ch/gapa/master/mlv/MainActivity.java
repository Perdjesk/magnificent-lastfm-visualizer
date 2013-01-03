package ch.gapa.master.mlv;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Caller.getInstance ().setCache ( new MemoryCache () );
		//
		// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build ();
		// StrictMode.setThreadPolicy ( policy );
		// String user = "jystha";
		// Collection<Artist> chart = User.getTopArtists ( user, API_KEY );
		// for ( Artist artist : chart ) {
		// Log.v ( "Artist", artist.getName () );
		// }

		// setContentView(new TouchExampleView(this));
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
