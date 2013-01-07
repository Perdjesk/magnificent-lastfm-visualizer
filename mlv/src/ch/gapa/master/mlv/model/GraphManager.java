package ch.gapa.master.mlv.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import ch.gapa.master.mlv.data.Action;
import ch.gapa.master.mlv.data.ActionFactory;
import ch.gapa.master.mlv.data.BusProvider;
import ch.gapa.master.mlv.data.Edge;
import ch.gapa.master.mlv.data.Graph;
import ch.gapa.master.mlv.data.Tree;
import ch.gapa.master.mlv.data.Tree.Node;
import ch.gapa.master.mlv.model.ArtistWrapper.Status;
import ch.gapa.master.mlv.view.TapEvent;

import com.squareup.otto.Subscribe;

import de.umass.lastfm.Period;

public final class GraphManager {

	private final Graph<ArtistWrapper> _graph;
	private final Tree<Action<ArtistWrapper>> _tree;
	private final Object LOCK = new Object();
	private final Paint _edgePaint = new Paint();
	private ArtistWrapper current;

	public enum TapType {
		SINGLE, DOUBLE;
	}

	public GraphManager(final String username, final Period period) {
		BusProvider.INSTANCE.getBus().register(this);
		_graph = new Graph<ArtistWrapper>();
		Action<ArtistWrapper> action = ActionFactory.createFirstAction(
				username, period);
		_tree = new Tree<Action<ArtistWrapper>>(action);
		action.execute(_graph, this);
		_edgePaint.setAntiAlias(true);
		_edgePaint.setColor(Color.WHITE);
		_edgePaint.setStrokeCap(Paint.Cap.ROUND);
		_edgePaint.setStrokeWidth(1);
	}

	public void draw(final Canvas canvas) {
		synchronized (LOCK) {
			// for every edge, draw line
			for (Edge<ArtistWrapper> edge : _graph.getEdges()) {
				Point pStart = edge.getStart().getPosition();
				Point pEnd = edge.getEnd().getPosition();
				if (edge.getStart().isFaded() || edge.getEnd().isFaded())
					_edgePaint.setAlpha(100);
				else
					_edgePaint.setAlpha(255);
				canvas.drawLine(pStart.x, pStart.y, pEnd.x, pEnd.y, _edgePaint);
			}
			// call every node to draw themselves
			for (ArtistWrapper wrapper : _graph.getVertices()) {
				wrapper.draw(canvas);
			}
		}
	}

	public void resetAlphas() {
		for (ArtistWrapper node : _graph.getVertices()) {
			node.setAlpha(255);
		}
	}

	public void fade(TapEvent event) {
		ArtistWrapper artistSelected = null;
		for (ArtistWrapper node : _graph.getVertices()) {
			if (node.contains(event.getLocation())) {
				artistSelected = node;
			}
		}
		if (artistSelected == null)
			return;
		for (ArtistWrapper node : _graph.getVertices()) {
			node.setAlpha(100);
		}
		Collection<ArtistWrapper> neighbors = _graph.neighbors(artistSelected);
		for (ArtistWrapper neighbor : neighbors) {
			neighbor.setAlpha(255);
		}
	}

	@Subscribe
	public void expand(final TapEvent event) {
		synchronized (LOCK) {
			for (ArtistWrapper node : _graph.getVertices()) {
				if (node.contains(event.getLocation())) {
					if (current != null) {
						current.setStatus(Status.NOT_SELECTED);
					}
					node.setStatus(Status.SELECTED);
					current = node;
					break;
				}
			}
			if (current != null) { // Current
				// node
				// pressed
				current.setStatus(Status.EXPAND);
				if (current.getStatus() != Status.EXPANDED) { // lancer une
					// action pour
					// expand
					Action<ArtistWrapper> action = ActionFactory
							.createExpandArtistAction(current);
					_tree.insert(action);
					action.execute(_graph, this);
				}
			}
		}
	}

	// TODO: call from somewhere when button (or else) to undo is pressed.
	public void undo() {
		_tree.getNode().getData().rollback(_graph, this);
		_tree.setNode(_tree.getNode().getParent());
	}

	// TODO: call from somewhere to get a list of actions to redo (if size == 1,
	// pass to redo method)
	public List<Action<ArtistWrapper>> getRedoList() {
		List<Node<Action<ArtistWrapper>>> nodes = _tree.getNode().getChildren();
		List<Action<ArtistWrapper>> actions = new ArrayList<Action<ArtistWrapper>>();
		for (Node<Action<ArtistWrapper>> node : nodes) {
			actions.add(node.getData());
		}
		return actions;
	}

	// TODO: call from somewhere when decision on which action to redo is
	// decided (or size == 1)
	public void redo(final Action<ArtistWrapper> action) {
		// for all children, check if equal, if that's the case, redo action and
		// set node to equal the correct node
		for (Node<Action<ArtistWrapper>> node : _tree.getNode().getChildren()) {
			if (node.getData().equals(action)) {
				node.getData().execute(_graph, this);
				_tree.setNode(node);
				break;
			}
		}
	}

	public void commitGraph(double kinetic) {
		synchronized (LOCK) {
			for (ArtistWrapper artist : _graph.getVertices()) {
				artist.commitPosition();
			}
		}
		// TODO: Check if kinetic calls for a new round of computation
	}

	public void addVerticesAndEdges(final Set<ArtistWrapper> vertices,
			final Set<Edge<ArtistWrapper>> edges) {
		synchronized (LOCK) {
			for (ArtistWrapper artist : vertices) {
				_graph.addVertex(artist);
			}
			for (Edge<ArtistWrapper> edge : edges) {
				_graph.addEdge(edge);
			}
		}
	}

	public void removeVerticesAndEdges(final Set<ArtistWrapper> vertices,
			final Set<Edge<ArtistWrapper>> edges) {
		synchronized (LOCK) {
			for (ArtistWrapper artist : vertices) {
				_graph.removeVertex(artist);
			}
		}
	}
}
