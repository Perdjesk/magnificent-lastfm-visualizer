package ch.gapa.master.mlv.data;

import static ch.gapa.master.mlv.model.Constants.API_KEY;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import ch.gapa.master.mlv.model.ArtistWrapper;
import ch.gapa.master.mlv.model.ArtistWrapper.Status;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Period;
import de.umass.lastfm.User;

public final class ActionFactory {

  private static final int GRAPH_LIMIT = 3;
  private static final int GRAPH_LIMIT_INIT = 5;

  private ActionFactory () {
    throw new AssertionError( "This class must not be initiated!" );
  }

  public static Action<ArtistWrapper> createExpandArtistAction ( final ArtistWrapper artist ) {
    return new ArtistAction( artist );
  }

  public static Action<ArtistWrapper> createFirstAction ( final String username, final Period period ) {
    return new FirstAction( username, period );
  }

  private static final class ArtistAction implements Action<ArtistWrapper> {

    private boolean done;
    /**
     * {@link Artist}
     */
    private final ArtistWrapper _artist;
    /**
     * {@link Artist} created once action is executed.
     */
    private Set<ArtistWrapper> _vertices;

    private ArtistAction ( final ArtistWrapper artist ) {
      this._artist = artist;
    }

    /**
		 * 
		 */
    public void execute ( final Graph<ArtistWrapper> graph ) {
      // Do the action
      if ( !done ) {
        final Callable<Set<ArtistWrapper>> DO = new Callable<Set<ArtistWrapper>>() {

          public Set<ArtistWrapper> call () throws Exception {
            done = true;
            Collection<Artist> similars = Artist.getSimilar( _artist.getArtist().getName(), GRAPH_LIMIT, API_KEY );
            for ( Artist similar : similars ) {
              ArtistWrapper wrapper = new ArtistWrapper( similar );
              if ( graph.addVertex( wrapper ) ) { // Does not exists in the graph
                _vertices.add( wrapper );
                graph.addEdge( new Edge<ArtistWrapper>( _artist, wrapper ) );
              }
            }
            return _vertices;
          }
        };
        new FutureTask<Set<ArtistWrapper>>( DO ).run();
      } else { // REDO by putting vertices back in graph
        for ( ArtistWrapper wrapper : _vertices ) {
          if ( graph.addVertex( wrapper ) ) { // Does not exists in the graph
            _vertices.add( wrapper );
            graph.addEdge( new Edge<ArtistWrapper>( _artist, wrapper ) );
          }
        }
      }
      _artist.setStatus( Status.EXPANDED );
    }

    /**
		 * 
		 */
    public void rollback ( final Graph<ArtistWrapper> graph ) {
      for ( ArtistWrapper wrapper : _vertices ) {
        graph.removeVertex( wrapper );
      }
      _artist.setStatus( Status.SELECTED );
    }

    /**
		 * 
		 */
    public String getDefinition () {
      StringBuilder sb = new StringBuilder( "Expand: " );
      sb.append( _artist.getArtist().getName() );
      return sb.toString();
    }

    @Override
    public boolean equals ( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( !( obj instanceof ArtistAction ) ) {
        return false;
      }
      ArtistAction action = (ArtistAction) obj;
      boolean equals = this._artist.equals( action._artist );
      return equals;
    }
  }

  private static final class FirstAction implements Action<ArtistWrapper> {

    private final String _username;
    private final Period _period;

    public FirstAction ( final String username, final Period period ) {
      _username = username;
      _period = period;
    }

    // TODO: ajouter un nombre limite initial
    @Override
    public void execute ( final Graph<ArtistWrapper> graph ) {
      final Callable<Void> DO = new Callable<Void>() {

        public Void call () throws Exception {
          Collection<Artist> initialArtists = User.getTopArtists( _username, _period, API_KEY );
          Set<ArtistWrapper> vertices = new HashSet<ArtistWrapper>();
          int count = 0;
          for ( Artist artist : initialArtists ) {
            ArtistWrapper wrapper = new ArtistWrapper( artist );
            graph.addVertex( wrapper );
            vertices.add( wrapper );
            if ( ++count >= GRAPH_LIMIT_INIT ) {
              break;
            }
          }
          // Add edges between each wrapper possible
          for ( ArtistWrapper wrapper : vertices ) {
            Collection<Artist> similars = Artist.getSimilar( wrapper.getArtist().getName(), API_KEY );
            for ( ArtistWrapper initial : vertices ) {
              if ( similars.contains( initial.getArtist() ) ) {
                graph.addEdge( new Edge<ArtistWrapper>( wrapper, initial ) );
              }
            }
          }
          return null;
        }
      };
      new FutureTask<Void>( DO ).run();
    }

    @Override
    public void rollback ( final Graph<ArtistWrapper> graph ) {
      // NOTHING
    }

    @Override
    public String getDefinition () {
      return "Graph creation: " + _username + ", (range = " + _period.toString() + " ).";
    }
  }
}
