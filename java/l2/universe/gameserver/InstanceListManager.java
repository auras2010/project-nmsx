package l2.universe.gameserver;

/**
 * Interface for managers of list of instances.
 * 
 * @author fordfrog
 */
public interface InstanceListManager
{
	/**
	 * Loads instances with their data from persistant format. This method has no side effect as
	 * calling methods of another instance manager.
	 */
	public void loadInstances();
	
	/**
	 * For each loaded instance, updates references to related instances.
	 */
	public void updateReferences();
	
	/**
	 * Activates instances so their setup is performed.
	 */
	public void activateInstances();
}