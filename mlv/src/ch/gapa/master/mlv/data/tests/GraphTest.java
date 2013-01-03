package ch.gapa.master.mlv.data.tests;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.gapa.master.mlv.data.Edge;
import ch.gapa.master.mlv.data.Graph;


public class GraphTest {

	private static final int ELEMENTS = 21;

	private static final int MOD = 4;

	private Graph<Integer> fullyConnectedGraph;

	private Graph<Integer> sparseGraph;

	@Test
	public void testInitNumberOfVertices () {
		Assert.assertTrue( fullyConnectedGraph.getVertexCount() == ELEMENTS );
		Assert.assertTrue( fullyConnectedGraph.getEdgeCount() == ( ELEMENTS * ( ( ELEMENTS - 1 ) ) / 2 ) );
		Assert.assertTrue( sparseGraph.getVertexCount() == ELEMENTS );
	}

	@Test
	public void testNeighbors1 () {
		Collection<Collection<Integer>> groups = fullyConnectedGraph.getNeighborGroups();
		Assert.assertTrue( groups.size() == 1 );
	}

	@Test
	public void testNeighbors2 () {
		Collection<Collection<Integer>> groups = sparseGraph.getNeighborGroups();
		Assert.assertTrue( groups.size() == MOD );
	}

	@Before
	public void setUp () throws Exception {
		fullyConnectedGraph = new Graph<Integer>();
		for ( int i = 0; i < ELEMENTS; i++ ) {
			fullyConnectedGraph.addVertex( i );
		}
		for ( int i = 0; i < ELEMENTS; i++ ) {
			for ( int j = 0; j < ELEMENTS; j++ ) {
				if ( j == i ) continue;
				fullyConnectedGraph.addEdge( new Edge<Integer>( i, j ) );
			}
		}
		sparseGraph = new Graph<Integer>();
		for ( int i = 0; i < ELEMENTS; i++ ) {
			sparseGraph.addVertex( i );
		}
		for ( int i = 0; i < ELEMENTS; i++ ) {
			for ( int j = ( i % MOD ); j < ELEMENTS; j += MOD ) {
				if ( j == i ) continue;
				sparseGraph.addEdge( new Edge<Integer>( i, j ) );
			}
		}
	}

	@After
	public void tearDown () throws Exception {
		fullyConnectedGraph = null;
	}
}
