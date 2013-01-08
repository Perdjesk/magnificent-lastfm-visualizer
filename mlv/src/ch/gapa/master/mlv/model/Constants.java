package ch.gapa.master.mlv.model;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

import de.umass.lastfm.Period;

public final class Constants {

  private Constants () {
    throw new AssertionError();
  }

  public static final String API_KEY = "a35b10d0b118131a390598f23414377c";
  public static final String DEFAULT_USER = "nordesk";
  public static final Period DEFAULT_PERIOD = Period.OVERALL;
  public static final int NODE_RADIUS = 20;
  public static final int INITIAL_DISTANCE_FACTOR = 5;
  public static final double SPRING_MINIMAL_LENGTH = 80.;
  public static final int SHIFT_X = 100;
  public static final int SHIFT_Y = 100;
  public static final double GRAVITY = 200000.0;
  public static final double HOOKE_K = 700.0;
  public static final double DAMPING = 0.1;
  public static final int MAX_GRAVITY = 1000000;
  public static final int MAX_HOOKE_K = 1500;
  public static final int MAX_DAMPING = 1000;
  public static final int GRAPH_LIMIT = 3;
  public static final int GRAPH_LIMIT_INIT = 5;
  public static final Map<String, Integer> tagColor = new HashMap<String, Integer>() {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    {
      put( "rock", Color.RED );
      put( "pop", Color.rgb( 0, 204, 0 ) );
      put( "metal", Color.rgb( 68, 68, 68 ) );
      put( "electronic", Color.rgb( 255, 102, 0 ) );
      put( "rap", Color.BLUE );
      put( "hip-hop", Color.BLUE );
      put( "jazz", Color.YELLOW );
      put( "reggae", Color.rgb( 255, 0, 255 ) );
      put( "ska", Color.rgb( 255, 0, 255 ) );
      put( "classic", Color.rgb( 0, 204, 204 ) );
      put( "classical", Color.rgb( 0, 204, 204 ) );
      put( "country", Color.rgb( 153, 51, 0 ) );
      put( "folk", Color.rgb( 153, 51, 0 ) );
      put( "world music", Color.rgb( 153, 51, 0 ) );
    };
  };
}
