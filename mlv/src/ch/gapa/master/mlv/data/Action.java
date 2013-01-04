package ch.gapa.master.mlv.data;

/**
 * The {@code Action} interface defines how to do/undo/redo based on Command pattern.The action can be initiated with
 * {@link ActionFactory}.
 * 
 * <p>
 * Each {@code Action} must be posted using {@code EventBus} architecture in order for it to be executed. The
 * {@link #execute()} method will then be called. To rollback,
 * 
 * <p>
 * The {@link #redo()} method must be called to get the {@code Action} list which then can be redo.
 * TODO: Attention, refonte pour execute sur le graphmanager pour qu'il puisse ajouter et remove les elements threadsafe
 */
public interface Action<T> {

  /**
   * Executes the {@code Action}, storing whatever is made to be rolled back.
   */
  void execute ( final Graph<T> graph );

  /**
   * Rollbacks the given {@code Action} to the previous state.
   */
  void rollback ( final Graph<T> graph );

  /**
   * Used in conjunction with {@link #redo()} to get all actions descriptions.
   * 
   * @return the action definition (e.g. expand 'Bon Iver' similarities)
   */
  String getDefinition ();
}
