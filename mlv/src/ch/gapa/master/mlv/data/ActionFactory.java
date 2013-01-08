package ch.gapa.master.mlv.data;

import static ch.gapa.master.mlv.model.Constants.API_KEY;
import static ch.gapa.master.mlv.model.Constants.GRAPH_LIMIT;
import static ch.gapa.master.mlv.model.Constants.GRAPH_LIMIT_INIT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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

    @Override
    public void execute ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      // Do the action
      _executor.submit( new Runnable() {

        public void run () {
          if ( !done ) {
            done = true;
            Collection<Artist> similars = Artist.getSimilar( _artist.getArtist().getName(), GRAPH_LIMIT, API_KEY );
            for ( Artist similar : similars ) {
              ArtistWrapper wrapper = new ArtistWrapper( similar );
              if ( !graph.hasVertex( wrapper ) ) { // Does not exists in the graph
                _vertices.add( wrapper );
                _edges.add( new Edge<ArtistWrapper>( _artist, wrapper ) );
              } else { // if similar does exist in graph ensure that an edge exist
                if ( !graph.isLinked( _artist, wrapper ) ) {
                  _edges.add( new Edge<ArtistWrapper>( _artist, graph.getVertex( wrapper ) ) );
                }
                // if ( ( !_edges.contains( new Edge<ArtistWrapper>( _artist, wrapper ) ) )
                // && ( !_edges.contains( new Edge<ArtistWrapper>( wrapper, _artist ) ) ) ) {
                // }
              }
            }
          }
          // else { // REDO by putting vertices back in graph
          // manager.addVerticesAndEdges( _vertices, _edges );
          // }
          manager.addVerticesAndEdges( _vertices, _edges ); // Do or redo, put back vertices and edges in graph
          _artist.setStatus( Status.EXPANDED );
        }
      } );
    }

    @Override
    public void rollback ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      _executor.submit( new Runnable() {

        public void run () {
          manager.removeVerticesAndEdges( _vertices, _edges );
          _artist.setStatus( Status.SELECTED );
        }
      } );
    }

    @Override
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

    @Override
    public void execute ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      _executor.submit( new Runnable() {

        public void run () {
          Collection<Artist> initialArtists = User.getTopArtists( _username, _period, API_KEY, GRAPH_LIMIT_INIT );
          Set<ArtistWrapper> vertices = new HashSet<ArtistWrapper>();
          Set<Edge<ArtistWrapper>> edges = new HashSet<Edge<ArtistWrapper>>();
          for ( Artist artist : initialArtists ) {
            ArtistWrapper wrapper = new ArtistWrapper( artist );
            vertices.add( wrapper );
          }
          // Add edges between each wrapper possible
          for ( ArtistWrapper wrapper : vertices ) {
            Collection<Artist> similars = Artist.getSimilar( wrapper.getArtist().getName(), GRAPH_LIMIT, API_KEY );
            for ( ArtistWrapper initial : vertices ) {
              if ( similars.contains( initial.getArtist() ) ) {
                edges.add( new Edge<ArtistWrapper>( wrapper, initial ) );
              }
            }
          }
          manager.firstAction( vertices, edges );
          // TODO: terminated, may call manager to start draw
        }
      } );
    }

    @Override
    public void rollback ( final Graph<ArtistWrapper> graph, final GraphManager manager ) {
      // NOTHING, it's the first action; no rollback possible
    }

    @Override
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
