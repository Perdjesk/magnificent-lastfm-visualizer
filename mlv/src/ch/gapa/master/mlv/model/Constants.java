package ch.gapa.master.mlv.model;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

import de.umass.lastfm.Period;

public final class Constants {
	private Constants() {
		throw new AssertionError();
	}

	public static final String API_KEY = "a35b10d0b118131a390598f23414377c";
	public static final String DEFAULT_USER = "nordesk";
	public static final Period DEFAULT_PERIOD = Period.OVERALL;
	public static final Map<String, Integer> tagColor = new HashMap<String, Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("rock",Color.RED);
			
			put("pop",Color.rgb(0, 204, 0));
			
			put("metal",Color.rgb( 68, 68, 68));
			
			put("electronic",Color.rgb( 255, 102, 0));
			
			put("rap",Color.BLUE);
			put("hip-hop",Color.BLUE);
			
			put("jazz",Color.YELLOW);
			
			put("reggae", Color.rgb(255, 0, 255));
			put("ska",Color.rgb(255, 0, 255));
			
			put("classic",Color.rgb(0, 204, 204));
			put("classical",Color.rgb(0, 204, 204));
			
			put("country",Color.rgb(153, 51, 0));
			put("folk",Color.rgb(153, 51, 0));
			put("world music",Color.rgb(153, 51, 0));
		}
		;
	};
}
