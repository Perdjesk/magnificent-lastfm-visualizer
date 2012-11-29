package ch.gapa.master.mlv.data;

/**
 * 
 * @param <V>
 */
public final class Edge<V> {

	/**
	 * The vertex from which the edge starts
	 */
	private final V _start;

	/**
	 * The vertex from which the edge ends
	 */
	private final V _end;

	/**
	 * Initializes {@code Edge} with two vertices.
	 * 
	 * @param start
	 *            the starting vertex
	 * @param end
	 *            the ending vertex
	 */
	public Edge ( final V start, final V end ) {
		this._start = start;
		this._end = end;
	}

	/**
	 * @return the starting vertex.
	 */
	public V getStart () {
		return _start;
	}

	/**
	 * @return the ending vertex.
	 */
	public V getEnd () {
		return _end;
	}

	/**
	 * 
	 * @param vertex
	 * @return
	 */
	public boolean hasLinkBetweenVertex ( final V vertex ) {
		return _start.equals( vertex ) || _end.equals( vertex );
	}

	/**
	 * Returns whether given objects equals to self Edge object. Override for {@link Graph} purposes.
	 * 
	 * @param obj
	 *            the object that may equal itself
	 * @return whether the object equals itself
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals ( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( !( obj instanceof Edge<?> ) ) {
			return false;
		}
		Edge<?> edge = (Edge<?>) obj;
		boolean equal = ( _start.equals( edge._start ) && _end.equals( edge._end ) ) || _start.equals( edge._end )
				&& _end.equals( edge._start );
		return equal;
	}

	/**
	 * Computes the hash value for {@link Graph} storing purposes.
	 * 
	 * @return the hash value from given object
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode () {
		int hash = 7;
		hash = ( 37 * hash ) + ( _start != null ? _start.hashCode() : 0 );
		hash = ( 37 * hash ) + ( _end != null ? _end.hashCode() : 0 );
		return 0;
	}

	/**
	 * @return the String value from given object
	 * @see Object#toString()
	 */
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder( "[Edge] {" );
		sb.append( _start.toString() ).append( "}, {" ).append( _end.toString() ).append( "}" );
		return sb.toString();
	}
}
