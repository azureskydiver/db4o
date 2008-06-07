package com.db4o.omplus.datalayer;

public interface IDbInterface {
	
	/**
	 * Get Stored Classes from the database
	 * @return 
	 */
	public void commit();
	
	/**
	 * Get Stored Classes from the database
	 * @return 
	 */
	public void close();
	
	/**
	 * Get Stored Classes from the database
	 * @return 
	 */
	public Object[] getStoredClasses();
	
	/**
	 * Get Stored Classes from the database
	 * @param className
	 * @return 
	 */
	public int getNumOfObjects(String className);
	
	/**
	 * Get object from the id given
	 * @param id
	 * @return 
	 */
	public Object getObjectById(long id);
	
	/**
	 * Refresh the object and get the values present in the database.
	 * @param obj
	 * @return 
	 */
	public void refreshObj(Object obj);

}
