package ch.gapa.master.mlv.model;

import static ch.gapa.master.mlv.model.Constants.DAMPING;
import static ch.gapa.master.mlv.model.Constants.GRAVITY;
import static ch.gapa.master.mlv.model.Constants.HOOKE_K;
import static ch.gapa.master.mlv.model.Constants.NODE_RADIUS;
import static ch.gapa.master.mlv.model.Constants.SHIFT_X;
import static ch.gapa.master.mlv.model.Constants.SHIFT_Y;
import static ch.gapa.master.mlv.model.Constants.SPRING_MINIMAL_LENGTH;
import android.graphics.Point;
import ch.gapa.master.mlv.data.Graph;

;
public final class GraphEngine {

  private final GraphManager _manager;
  private double _kinetic = .0;

  public GraphEngine ( final GraphManager manager ) {
    _manager = manager;
  }

  public void initGraphPositions ( final Graph<ArtistWrapper> graph ) {
    int vCount = graph.getVertexCount();
    double R = ( 3.0 * vCount * NODE_RADIUS ) / ( 2.0 * Math.PI ); // Radius
    double alpha = 0;
    int num = 0;
    for ( ArtistWrapper artist : graph.getVertices() ) {
      alpha = ( 2.0 * Math.PI / vCount ) * (double) num;
      int sgnX = 1;
      int sgnY = -1;
      double x = sgnX * R * Math.cos( alpha ) + R + NODE_RADIUS + SHIFT_X;
      double y = sgnY * R * Math.sin( alpha ) + R + NODE_RADIUS + SHIFT_Y;
      artist.setPosition( new Point( (int) x, (int) y ) ); // set x,y pos to artist
      num++;
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
    _manager.commitGraph( _kinetic );
  }
}
