package com.db4o.omplus.datalayer.queryresult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.db4o.ObjectContainer;
import com.db4o.omplus.datalayer.DbInterfaceImpl;
import com.db4o.omplus.datalayer.ModifyObject;

@SuppressWarnings("unchecked")
public class QueryResultTableModifiedList
{
	
	private HashSet <Object>modifiedObjList;
	
	private HashMap <Object, Boolean>deleteObjList;
	
	public QueryResultTableModifiedList()
	{
		modifiedObjList = new HashSet<Object>();
		deleteObjList = new HashMap<Object, Boolean>();
	}
	
	public HashSet<Object> getModifiedObjList()
	{
		return modifiedObjList;
	}
	
	public void addObjectToList(Object obj)
	{
		if(obj!=null)
			modifiedObjList.add(obj);
	}
	
	public void clearModifiedObjList()
	{
		int length = modifiedObjList.size();
		if(length > 0){
			modifiedObjList.clear();
		}
		
	}
	
	public void clearDeletedObjList()
	{
		int length = deleteObjList.size();
		if(length > 0){
			deleteObjList.clear();
		}
	}
	
	public void writeToDB()
	{
		ObjectContainer db = getOC();
		if(db != null){
			Iterator<Object> iterator = modifiedObjList.iterator();
			while(iterator.hasNext())
			{
				Object obj = iterator.next();
				db.store(obj);	
			}
			db.commit();
			clearModifiedObjList();
		}
	}
		
	public void deleteFromDB()
	{
		ObjectContainer db = getOC();
		if(db != null){
			deteleObjects(db);
		}
	}


	private void deteleObjects(ObjectContainer db) 
	{
		Iterator iterator = deleteObjList.keySet().iterator();
		ModifyObject modify = new ModifyObject();
		while(iterator.hasNext()) {
			Object keyObj = iterator.next();
			Boolean cascadeOnDel = deleteObjList.get(keyObj);
			if(cascadeOnDel){
				modify.cascadeOnDelete(keyObj, db);
			} else {
				db.delete(keyObj);
				db.ext().purge(keyObj);
			}
			
		}
		db.commit();
		clearDeletedObjList();
	}

	public HashMap<Object, Boolean> getDeleteObjList() 
	{
		return deleteObjList;
	}

	public void setDeleteObjList(HashMap<Object, Boolean> deleteObjList) 
	{
		this.deleteObjList = deleteObjList;
	}

	public void addToDeleteList(Object obj, Boolean value)
	{
		if(obj!=null && value != null)
			deleteObjList.put(obj, value);
	}

	public void refresh()
	{
		ObjectContainer db = getOC();
		if(db != null){
			Iterator<Object> iterator = modifiedObjList.iterator();
			while(iterator.hasNext())
			{
				Object obj = iterator.next();
				db.ext().refresh(obj, 2);			
			}
			clearModifiedObjList();
		}
	}
	
	private ObjectContainer getOC()
	{
		return DbInterfaceImpl.getInstance().getDB();
	}
}
