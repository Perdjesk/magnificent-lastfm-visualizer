package ch.gapa.master.mlv;

import java.util.Collection;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.User;
import de.umass.lastfm.cache.MemoryCache;

public class MainActivity extends Activity {
	private static final String API_KEY = "a35b10d0b118131a390598f23414377c";

	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_main );

		Caller.getInstance ().setCache ( new MemoryCache () );

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build ();
		StrictMode.setThreadPolicy ( policy );
		String user = "jystha";
		Collection<Artist> chart = User.getTopArtists ( user, API_KEY );
		for ( Artist artist : chart ) {
			Log.v ( "Artist", artist.getName () );
		}

		// setContentView(new TouchExampleView(this));
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
