package ch.gapa.master.mlv.data;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
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
	 * Returns each neighbors group within the graph. Perform a breadth-first search to find neighbors of a given
	 * vertex.
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
	 *            the initial vertex
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
	 * @param vertex
	 */
	private void removeEdges ( final V vertex ) {
		for ( Edge<V> edge : _edges ) {
			if ( vertex.equals( edge.getStart() ) || vertex.equals( edge.getEnd() ) ) {
				removeEdge( edge );
			}
		}
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder( "[Graph] (vertices=" ).append( getVertexCount() ).append( ", edges=" )
				.append( getEdgeCount() ).append( ")\n" );
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