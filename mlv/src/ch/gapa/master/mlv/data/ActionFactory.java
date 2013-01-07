package ch.gapa.master.mlv.data;

import static ch.gapa.master.mlv.model.Constants.API_KEY;
import static ch.gapa.master.mlv.model.Constants.GRAPH_LIMIT;
import static ch.gapa.master.mlv.model.Constants.GRAPH_LIMIT_INIT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.gapa.master.mlv.model.ArtistWrapper;
import ch.gapa.master.mlv.model.ArtistWrapper.Status;
import ch.gapa.master.mlv.model.GraphManager;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Period;
import de.umass.lastfm.User;

public final class ActionFactory {

  private static final ExecutorService _executor = Executors.newFixedThreadPool( 1 );

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
    private Set<Edge<ArtistWrapper>> _edges;

    private ArtistAction ( final ArtistWrapper artist ) {
      this._artist = artist;
      _vertices = new HashSet<ArtistWrapper>();
      _edges = new HashSet<Edge<ArtistWrapper>>();
    }

    /**
		 * 
		 */
    public void execute ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      // Do the action
      if ( !done ) {
        final Callable<Void> DO = new Callable<Void>() {

          public Void call () throws Exception {
            done = true;
            Collection<Artist> similars = Artist.getSimilar( _artist.getArtist().getName(), GRAPH_LIMIT, API_KEY );
            for ( Artist similar : similars ) {
              ArtistWrapper wrapper = new ArtistWrapper( similar );
              if ( ! graph.hasVertex( wrapper ) ) { // Does not exists in the graph
                _vertices.add( wrapper );
                _edges.add( new Edge<ArtistWrapper>( _artist, wrapper ) );
              }
              else { // if similar does exist in graph ensure that an edge exist 
					if ((!_edges.contains(new Edge<ArtistWrapper>(_artist, wrapper)))
							&& (!_edges.contains(new Edge<ArtistWrapper>(wrapper, _artist)))) {
						_edges.add( new Edge<ArtistWrapper>( _artist, graph.getVertex(wrapper) ) );
            	  }
              }
            }
            manager.addVerticesAndEdges( _vertices, _edges );
            return null;
          }
        };
        _executor.submit( DO );
      } else { // REDO by putting vertices back in graph
        manager.addVerticesAndEdges( _vertices, _edges );
      }
      _artist.setStatus( Status.EXPANDED );
    }

    /**
		 * 
		 */
    public void rollback ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      manager.removeVerticesAndEdges( _vertices, _edges );
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

    @Override
    public int hashCode () {
      return _artist.hashCode();
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
    // @Override
    public void execute ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      final Callable<Void> DO = new Callable<Void>() {

        public Void call () throws Exception {
          Collection<Artist> initialArtists = User.getTopArtists( _username, _period, API_KEY, GRAPH_LIMIT_INIT );
          Set<ArtistWrapper> vertices = new HashSet<ArtistWrapper>();
          for ( Artist artist : initialArtists ) {
            ArtistWrapper wrapper = new ArtistWrapper( artist );
            graph.addVertex( wrapper );
            vertices.add( wrapper );
          }
          // Add edges between each wrapper possible
          for ( ArtistWrapper wrapper : vertices ) {
            Collection<Artist> similars = Artist.getSimilar( wrapper.getArtist().getName(), GRAPH_LIMIT, API_KEY );
            for ( ArtistWrapper initial : vertices ) {
              if ( similars.contains( initial.getArtist() ) ) {
                graph.addEdge( new Edge<ArtistWrapper>( wrapper, initial ) );
              }
            }
          }
          return null;
          // TODO: terminated, may call manager to start draw
        }
      };
      _executor.submit( DO );
    }

    // @Override
    public void rollback ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      // NOTHING
    }

    // @Override
    public String getDefinition () {
      return "Graph creation: " + _username + ", (range = " + _period.toString() + " ).";
    }

    @Override
    public boolean equals ( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( !( obj instanceof ArtistAction ) ) {
        return false;
      }
      FirstAction action = (FirstAction) obj;
      boolean equals = this._username.equals( action._username );
      equals &= this._period.equals( action._period );
      return equals;
    }

    @Override
    public int hashCode () {
      return ( 7 * _username.hashCode() ) ^ _period.hashCode();
    }
  }
}
