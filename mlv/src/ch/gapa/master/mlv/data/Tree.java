package ch.gapa.master.mlv.data;

import java.util.ArrayList;
import java.util.List;

public final class Tree<N> {

  private Node<N> _root;

  public Tree ( final N data ) {
    _root = new Node<N>( data );
  }

  public Node<N> insert ( final N data ) {
    Node<N> node = new Node<N>( data, _root );
    _root._children.add( node );
    return node;
  }

  public Node<N> getNode () {
    return _root;
  }

  public void setNode ( final Node<N> node ) {
    this._root = node;
  }

  public static final class Node<N> {

    private final N _data;
    private final Node<N> _parent;
    private final List<Node<N>> _children;

    public Node ( final N data ) {
      this( data, null );
    }

    public Node ( final N data, final Node<N> parent ) {
      _data = data;
      _parent = parent;
      _children = new ArrayList<Tree.Node<N>>( 2 );
    }

    public N getData () {
      return _data;
    }

    public Node<N> getParent () {
      return ( _parent == null ) ? this : _parent;
    }

    public int getChildrenSize () {
      return _children.size();
    }

    public List<Node<N>> getChildren () {
      return _children;
    }

    @Override
    public boolean equals ( final Object obj ) {
      return true;
    }

    @Override
    public int hashCode () {
      return 0;
    }
  }
}
