package ch.gapa.master.mlv.model;

import static ch.gapa.master.mlv.model.Constants.CIRCLE_BRANCHES;
import static ch.gapa.master.mlv.model.Constants.DAMPING;
import static ch.gapa.master.mlv.model.Constants.DISTANCE_FACTOR;
import static ch.gapa.master.mlv.model.Constants.GRAVITY;
import static ch.gapa.master.mlv.model.Constants.HOOKE_K;
import static ch.gapa.master.mlv.model.Constants.INITIAL_DISTANCE_FACTOR;
import static ch.gapa.master.mlv.model.Constants.NODE_RADIUS;
import static ch.gapa.master.mlv.model.Constants.RUN_AFTER_STABILISATION;
import static ch.gapa.master.mlv.model.Constants.SHIFT_X;
import static ch.gapa.master.mlv.model.Constants.SHIFT_Y;
import static ch.gapa.master.mlv.model.Constants.SPRING_MINIMAL_LENGTH;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import android.graphics.Point;
import ch.gapa.master.mlv.data.Edge;
import ch.gapa.master.mlv.data.Graph;

;
public final class GraphEngine {

  private final GraphManager _manager;
  private final Random _rand;
  private int _incr;
  private double _kinetic = .0;
  private double _oldKinetic = .0;

  public GraphEngine ( final GraphManager manager ) {
    _manager = manager;
    _incr = 0;
    _rand = new Random();
  }

  public void initGraphPositions ( final Graph<ArtistWrapper> graph ) {
    int vCount = graph.getVertexCount();
    double R = ( INITIAL_DISTANCE_FACTOR * vCount * NODE_RADIUS ) / ( 2.0 * Math.PI ); // Radius => 5 * 5 * 20 / 6.34 =
                                                                                       // 78 pixel radius
    double alpha = .0;
    int num = 0;
    for ( ArtistWrapper artist : graph.getVertices() ) {
      alpha = ( 2.0 * Math.PI / vCount ) * (double) num;
      int sgnX = 1;
      int sgnY = -1;
      double x = sgnX * R * Math.cos( alpha ) + R + NODE_RADIUS + SHIFT_X;
      double y = sgnY * R * Math.sin( alpha ) + R + NODE_RADIUS + SHIFT_Y;
      artist.setPosition( new Point( (int) x, (int) y ) ); // set x,y pos to artist
      artist.setPlacement( true );
      num++;
    }
  }

  public void initNewVertices ( final Graph<ArtistWrapper> graph, final Set<ArtistWrapper> vertices ) {
    // get all vertices connected to vertex
    // compute centroid
    // from centroid, try to place it in a circle fashion by checking for overlapping
    // if no overlapping, put it
    // if a full circle done, increase radius (double)
    for ( ArtistWrapper artist : vertices ) {
      double x = .0;
      double y = .0;
      Collection<Edge<ArtistWrapper>> edges = graph.getEdges( artist );
      for ( Edge<ArtistWrapper> edge : edges ) {
        ArtistWrapper other = edge.getStart().equals( artist ) ? edge.getEnd() : edge.getStart();
        Point posOther = other.getPosition();
        x += posOther.x;
        y += posOther.y;
      }
      x /= edges.size();
      y /= edges.size();
      double R = ( DISTANCE_FACTOR * NODE_RADIUS ) / ( 2.0 * Math.PI );
      boolean placement = false;
      do {
        double alpha = .0;
        final int init = _rand.nextInt( CIRCLE_BRANCHES );
        circle: for ( int i = 0; i < CIRCLE_BRANCHES; i++ ) {
          alpha = ( 2.0 * Math.PI / CIRCLE_BRANCHES ) * (double) ( ( i + init ) % CIRCLE_BRANCHES );
          int sgnX = 1;
          int sgnY = -1;
          double posX = sgnX * R * Math.cos( alpha ) + NODE_RADIUS + x;
          double posY = sgnY * R * Math.sin( alpha ) + NODE_RADIUS + y;
          Point newPos = new Point( (int) posX, (int) posY );
          for ( ArtistWrapper other : graph.getVertices() ) {
            if ( other.contains( newPos ) ) {
              continue circle; // try new position
            }
          }
          // finished all artists, position is correct, yay!
          artist.setPosition( newPos );
          placement = true;
        }
        R *= 2;
      } while ( !placement );
      artist.setPlacement( placement );
    }
  }

  public void computeStep ( final Graph<ArtistWrapper> graph ) {
    _kinetic = 0;
    for ( ArtistWrapper vertex : graph.getVertices() ) { // For each node, compute gravity and hooke
      double sumGravityFx = .0;
      double sumGravityFy = .0;
      double sumHookeFx = .0;
      double sumHookeFy = .0;
      Point posVertex = vertex.getPosition();
      // Gravity computation
      for ( ArtistWrapper other : graph.getVertices() ) {
        if ( vertex.equals( other ) ) { // Same vertex
          continue;
        }
        Point posOther = other.getPosition();
        double dist = Math.sqrt( Math.pow( posVertex.x - posOther.x, 2 ) + Math.pow( posVertex.y - posOther.y, 2 ) ); // Eclidean
                                                                                                                      // distance
        if ( dist < 2 * NODE_RADIUS ) { // Distance is too great
          dist = 2 * NODE_RADIUS;
        }
        if ( Double.compare( dist, .0 ) == 0 ) {
          continue;
        }
        double gravityF = (double) 1 / Math.pow( dist, 2 ) * GRAVITY; // Gravity scalar
        _kinetic += Math.abs( gravityF );
        double gravityFx = gravityF * ( -posOther.x + posVertex.x ) / dist;
        double gravityFy = gravityF * ( -posOther.y + posVertex.y ) / dist;
        sumGravityFx += gravityFx;
        sumGravityFy += gravityFy;
      }
      // Each node direclty connected, compute Hooke law
      for ( ArtistWrapper neighbor : graph.directNeighbors( vertex ) ) {
        Point posOther = neighbor.getPosition();
        double dist = Math.sqrt( Math.pow( posVertex.x - posOther.x, 2 ) + Math.pow( posVertex.y - posOther.y, 2 ) );
        if ( Double.compare( dist, .0 ) == 0 ) {
          continue;
        }
        double hookeF = -(double) HOOKE_K * ( dist - SPRING_MINIMAL_LENGTH ); // Hooke law
        _kinetic += Math.abs( hookeF );
        double hookeFx = hookeF * ( -posOther.x + posVertex.x ) / dist;
        double hookeFy = hookeF * ( -posOther.y + posVertex.y ) / dist;
        sumHookeFx += hookeFx;
        sumHookeFy += hookeFy;
      }
      double dx = ( sumGravityFx + sumHookeFx ) * DAMPING;
      double dy = ( sumGravityFy + sumHookeFy ) * DAMPING;
      vertex.updateTemporaryPosition( (int) dx, (int) dy );
    }
    boolean commit = true;
    if ( Double.compare( _oldKinetic, _kinetic ) == 0 && _incr >= RUN_AFTER_STABILISATION ) {
      commit = false;
    }
    _incr++;
    _manager.commitGraph( commit );
    _oldKinetic = _kinetic;
  }
}
