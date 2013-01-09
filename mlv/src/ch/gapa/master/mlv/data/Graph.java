package ch.gapa.master.mlv.data;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

/**
 * 
 * @param <V>
 */
public final class Graph<V> {

  /**
   * Group of edges linking vertices together
   */
  private final Set<Edge<V>> _edges;
  /**
   * Group of vertices within the graph
   */
  private final Set<V> _vertices;

  /**
   * Initializes {@code Graph} class.
   */
  public Graph () {
    _edges = new HashSet<Edge<V>>();
    _vertices = new HashSet<V>();
  }

  /**
   * Returns each neighbors group within the graph. Perform a breadth-first search to find neighbors of a given vertex.
   * 
   * @return neighbors groups linked together
   */
  public Collection<Collection<V>> getNeighborGroups () {
    Collection<Collection<V>> groups = new HashSet<Collection<V>>();
    Queue<V> queue = new ArrayDeque<V>();
    queue.addAll( _vertices );
    while ( !queue.isEmpty() ) {
      V vertex = queue.poll();
      Collection<V> neighbors = neighbors( vertex );
      groups.add( neighbors );
      queue.removeAll( neighbors );
    }
    return groups;
  }

  /**
   * Breadth-first search of all neighbors of given vertex.
   * 
   * @param vertex
   *          the initial vertex
   * @return a set of all vertex neighbors
   */
  public Collection<V> neighbors ( final V vertex ) {
    Set<V> neighbors = new HashSet<V>();
    Set<V> visited = new HashSet<V>();
    Queue<Edge<V>> queue = new ArrayDeque<Edge<V>>();
    neighbors.add( vertex );
    visited.add( vertex );
    queue.addAll( _edges );
    while ( !queue.isEmpty() ) {
      Edge<V> edge = queue.poll();
      for ( V currentNeighbors : visited ) {
        if ( edge.hasLinkBetweenVertex( currentNeighbors ) ) {
          neighbors.add( edge.getStart() );
          neighbors.add( edge.getEnd() );
        }
      }
      neighbors.addAll( visited );
    }
    return neighbors;
  }

  public Collection<V> directNeighbors ( final V vertex ) {
    Set<V> directNeighbors = new HashSet<V>();
    for ( Edge<V> edge : _edges ) {
      if ( edge.hasLinkBetweenVertex( vertex ) ) {
        V neighbor = edge.getStart().equals( vertex ) ? edge.getEnd() : edge.getStart();
        directNeighbors.add( neighbor );
      }
    }
    return directNeighbors;
  }

  /**
   * 
   * @param vertex
   * @return
   */
  public boolean addVertex ( final V vertex ) {
    return _vertices.add( vertex );
  }

  /**
   * 
   * @param edge
   * @return
   */
  public boolean addEdge ( final Edge<V> edge ) {
    return _edges.add( edge );
  }

  /**
   * 
   * @param edge
   */
  public void removeEdge ( final Edge<V> edge ) {
    _edges.remove( edge );
  }

  /**
   * 
   * @param vertex
   */
  public void removeVertex ( final V vertex ) {
    removeEdges( vertex );
    _vertices.remove( vertex );
  }

  /**
   * 
   * @param edge
   * @return
   */
  public boolean hasEdge ( final Edge<V> edge ) {
    return _edges.contains( edge );
  }

  /**
   * 
   * @param vertex
   * @return
   */
  public boolean hasVertex ( final V vertex ) {
    return _vertices.contains( vertex );
  }

  /**
   * 
   * @param vertex
   * @return
   */
  public V getVertex ( final V vertex ) {
    for ( V v : _vertices ) {
      if ( v.equals( vertex ) ) return v;
    }
    return null;
  }

  /**
   * 
   * @param vertex
   * @return
   */
  public Collection<Edge<V>> getEdges ( final V vertex ) {
    Set<Edge<V>> edges = new HashSet<Edge<V>>();
    for ( Edge<V> edge : _edges ) {
      if ( edge.hasLinkBetweenVertex( vertex ) ) {
        edges.add( edge );
      }
    }
    return edges;
  }

  /**
   * 
   * @param vertex1
   * @param vertex2
   * @return
   */
  public boolean isLinked ( final V vertex1, final V vertex2 ) {
    Collection<Edge<V>> edges = getEdges( vertex1 );
    for ( Edge<V> edge : edges ) {
      if ( edge.hasLinkBetweenVertex( vertex2 ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public int getVertexCount () {
    return _vertices.size();
  }

  /**
   * 
   * @return
   */
  public int getEdgeCount () {
    return _edges.size();
  }

  /**
   * 
   * @return
   */
  public Collection<V> getVertices () {
    return _vertices;
  }

  /**
   * 
   * @return
   */
  public Collection<Edge<V>> getEdges () {
    return _edges;
  }

  /**
   * 
   * @param vertex
   */
  private void removeEdges ( final V vertex ) {
	  Iterator<Edge<V>> iter = _edges.iterator();
	  Edge<V> edge = null;
	  while (iter.hasNext()){
		  edge = iter.next();
		  if ( vertex.equals( edge.getStart() ) || vertex.equals( edge.getEnd() ) ) {
		        iter.remove();
		      }
	  }
  }

  @Override
  public String toString () {
    StringBuilder sb = new StringBuilder( "[Graph] (vertices=" ).append( getVertexCount() ).append( ", edges=" ).append( getEdgeCount() )
        .append( ")\n" );
    sb.append( "Vertices: { " );
    for ( V vertex : _vertices ) {
      sb.append( vertex.toString() ).append( " " );
    }
    sb.append( "}\n" );
    sb.append( "Edges: {\n " );
    for ( Edge<V> edge : _edges ) {
      sb.append( "\t" ).append( edge ).append( "\n" );
    }
    sb.append( "}" );
    return sb.toString();
  }
}
