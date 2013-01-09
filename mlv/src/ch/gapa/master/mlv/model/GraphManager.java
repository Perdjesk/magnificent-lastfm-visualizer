package ch.gapa.master.mlv.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ch.gapa.master.mlv.MainActivity;
import ch.gapa.master.mlv.data.Action;
import ch.gapa.master.mlv.data.ActionFactory;
import ch.gapa.master.mlv.data.Edge;
import ch.gapa.master.mlv.data.Graph;
import ch.gapa.master.mlv.data.Tree;
import ch.gapa.master.mlv.data.Tree.Node;
import ch.gapa.master.mlv.model.ArtistWrapper.Status;
import ch.gapa.master.mlv.view.TapEvent;
import de.umass.lastfm.Period;

public final class GraphManager {

  private final GraphEngine _engine;
  private final Graph<ArtistWrapper> _graph;
  private final Tree<Action<ArtistWrapper>> _tree;
  private final Object LOCK = new Object();
  private final Paint _edgePaint = new Paint();
  private final ExecutorService _executor = Executors.newFixedThreadPool( 2 );
  private final AtomicBoolean _running;
  private ArtistWrapper current;

  public enum TapType {
    SINGLE, DOUBLE, LONG;
  }

  /**
   * 
   * @param username
   * @param period
   */
  public GraphManager ( final String username, final Period period ) {
    _running = new AtomicBoolean( false );
    _engine = new GraphEngine( this );
    _graph = new Graph<ArtistWrapper>();
    Action<ArtistWrapper> action = ActionFactory.createFirstAction( username, period );
    _tree = new Tree<Action<ArtistWrapper>>( action );
    action.execute( _graph, this );
    _edgePaint.setAntiAlias( true );
    _edgePaint.setColor( Color.WHITE );
    _edgePaint.setStrokeCap( Paint.Cap.ROUND );
    _edgePaint.setHinting( Paint.HINTING_ON );
    _edgePaint.setStrokeWidth( 1.3f );
  }

  /**
   * 
   * @param canvas
   */
  public void draw ( final Canvas canvas ) {
    synchronized ( LOCK ) {
      // for every edge, draw line
      for ( Edge<ArtistWrapper> edge : _graph.getEdges() ) {
        Point pStart = edge.getStart().getPosition();
        Point pEnd = edge.getEnd().getPosition();
        if ( !edge.getStart().hasPlacement() || !edge.getEnd().hasPlacement() ) { // Not placed, do not draw edge!
          continue;
        }
        if ( edge.getStart().isFaded() || edge.getEnd().isFaded() ) {
          _edgePaint.setAlpha( 100 );
        } else {
          _edgePaint.setAlpha( 255 );
        }
        canvas.drawLine( pStart.x, pStart.y, pEnd.x, pEnd.y, _edgePaint );
      }
      // call every node to draw themselves
      for ( ArtistWrapper wrapper : _graph.getVertices() ) {
        wrapper.draw( canvas );
      }
    }
  }

  /**
   * 
   */
  public void resetAlphas () {
    _executor.execute( new Runnable() {

      public void run () {
        synchronized ( LOCK ) {
          for ( ArtistWrapper node : _graph.getVertices() ) {
            node.setAlpha( 255 );
          }
        }
      }
    } );
  }

  /**
   * 
   * @param event
   */
  public void fade ( final TapEvent event ) {
    _executor.execute( new Runnable() {

      public void run () {
        synchronized ( LOCK ) {
          ArtistWrapper artistSelected = null;
          for ( ArtistWrapper node : _graph.getVertices() ) {
            if ( node.contains( event.getLocation() ) ) {
              artistSelected = node;
            }
          }
          if ( artistSelected == null ) return;
          for ( ArtistWrapper node : _graph.getVertices() ) {
            node.setAlpha( 100 );
          }
          Collection<ArtistWrapper> neighbors = _graph.neighbors( artistSelected );
          for ( ArtistWrapper neighbor : neighbors ) {
            neighbor.setAlpha( 255 );
          }
        }
      }
    } );
  }

  /**
   * 
   * @param event
   */
  public void expand ( final TapEvent event ) {
    final GraphManager manager = this;
    _executor.execute( new Runnable() {

      public void run () {
        synchronized ( LOCK ) {
          for ( ArtistWrapper node : _graph.getVertices() ) {
            if ( node.contains( event.getLocation() ) ) {
              if ( current != null ) {
                current.setStatus( Status.NOT_SELECTED );
              }
              node.setStatus( Status.SELECTED );
              current = node;
              break;
            }
          }
          if ( current != null ) { // Current node pressed
            current.setStatus( Status.EXPAND );
            if ( current.getStatus() != Status.EXPANDED ) { // lancer une action pourexpand
              Action<ArtistWrapper> action = ActionFactory.createExpandArtistAction( current );
              Node<Action<ArtistWrapper>> node = _tree.insert( action );
              _tree.setNode( node );
              action.execute( _graph, manager );
            }
          }
        }
      }
    } );
  }

  public void detailsOfArtist ( final TapEvent event ) {
    _executor.execute( new Runnable() {

      public void run () {
        synchronized ( LOCK ) {
          ArtistWrapper selected = null;
          for ( ArtistWrapper node : _graph.getVertices() ) {
            if ( node.contains( event.getLocation() ) ) {
              selected = node;
              break;
            }
          }
          if ( selected != null ) dialogArtistDetails( selected );
        }
      }
    } );
  }

  private void dialogArtistDetails ( final ArtistWrapper artist ) {
    MainActivity.mainActivity.runOnUiThread( new Runnable() {

      public void run () {
        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.mainActivity );
        final WebView webview = new WebView( MainActivity.mainActivity );
        String url = artist.getArtist().getUrl();
        if ( !url.startsWith( "http://" ) ) {
          url = "http://" + url;
        }
        webview.loadUrl( url );
        webview.resumeTimers();
        webview.getSettings().setSupportZoom( true );
        webview.getSettings().setBuiltInZoomControls( true );
        webview.setWebViewClient( new WebViewClient() {

          @Override
          public boolean shouldOverrideUrlLoading ( WebView view, String url ) {
            view.loadUrl( url );
            return true;
          }
        } );
        builder.setTitle( "Artist details" ).setNegativeButton( "Return to graph", new DialogInterface.OnClickListener() {

          @Override
          public void onClick ( DialogInterface dialog, int id ) {
            dialog.dismiss();
            webview.pauseTimers();
          }
        } ).setOnKeyListener( new DialogInterface.OnKeyListener() {

          @Override
          public boolean onKey ( DialogInterface dialog, int keyCode, KeyEvent event ) {
            if ( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled() ) {
              webview.goBack();
              return true;
            }
            return false;
          }
        } ).setView( webview );
        AlertDialog dialog = builder.create();
        dialog.show();
      }
    } );
  }

  /**
   * 
   */
  public boolean undo () {
    if ( _tree.getNode().getParent() == _tree.getNode() ) return false;
    _tree.getNode().getData().rollback( _graph, this );
    _tree.setNode( _tree.getNode().getParent() );
    return true;
  }

  /**
   * 
   * @return
   */
  public List<Action<ArtistWrapper>> getRedoList () {
    List<Node<Action<ArtistWrapper>>> nodes = _tree.getNode().getChildren();
    List<Action<ArtistWrapper>> actions = new ArrayList<Action<ArtistWrapper>>( nodes.size() );
    for ( Node<Action<ArtistWrapper>> node : nodes ) {
      actions.add( node.getData() );
    }
    return actions;
  }

  /**
   * 
   * @param action
   */
  public void redo ( final Action<ArtistWrapper> action ) {
    // for all children, check if equal, if that's the case, redo action and
    // set node to equal the correct node
    for ( Node<Action<ArtistWrapper>> node : _tree.getNode().getChildren() ) {
      if ( node.getData().equals( action ) ) {
        node.getData().execute( _graph, this );
        _tree.setNode( node );
        break;
      }
    }
  }

  /**
   * 
   * @param next
   */
  public void commitGraph ( boolean next ) {
    _running.set( false );
    synchronized ( LOCK ) {
      for ( ArtistWrapper artist : _graph.getVertices() ) {
        artist.commitPosition();
      }
    }
    if ( next && !_running.getAndSet( true ) ) {
      _executor.execute( new Runnable() {

        @Override
        public void run () {
          _engine.computeStep( _graph );
        }
      } );
    }
  }

  /**
   * 
   * @param vertices
   * @param edges
   */
  public void addVerticesAndEdges ( final Set<ArtistWrapper> vertices, final Set<Edge<ArtistWrapper>> edges ) {
    synchronized ( LOCK ) {
      for ( ArtistWrapper artist : vertices ) {
        _graph.addVertex( artist );
      }
      for ( Edge<ArtistWrapper> edge : edges ) {
        _graph.addEdge( edge );
      }
      _engine.initNewVertices( _graph, vertices );
      commitGraph( true ); // automatically launch one step at least !
    }
  }

  /**
   * 
   * @param vertices
   * @param edges
   */
  public void removeVerticesAndEdges ( final Set<ArtistWrapper> vertices, final Set<Edge<ArtistWrapper>> edges ) {
    synchronized ( LOCK ) {
      for ( ArtistWrapper artist : vertices ) {
        _graph.removeVertex( artist );
      }
    }
  }

  /**
   * 
   * @param vertices
   * @param edges
   */
  public void firstAction ( final Set<ArtistWrapper> vertices, final Set<Edge<ArtistWrapper>> edges ) {
    synchronized ( LOCK ) {
      for ( ArtistWrapper artist : vertices ) {
        _graph.addVertex( artist );
      }
      for ( Edge<ArtistWrapper> edge : edges ) {
        _graph.addEdge( edge );
      }
      _engine.initGraphPositions( _graph );
    }
    // TODO: May indicate that graph placement is done
  }
}
