package com.db4o.omplus.connection;

import java.util.ArrayList;

import com.db4o.omplus.datalayer.OMEData;
import com.db4o.omplus.datalayer.queryBuilder.OMQuery;

@SuppressWarnings("unchecked")
public class RecentQueries {

	private final String KEY_GQ = "GLOBAL_QUERIES";
	
	private OMEData data = OMEData.getInstance();


	private ArrayList<OMQuery> getRecentQueriesForDB() {
		ArrayList<OMQuery> queryList = data.getDataValue(KEY_GQ);
	    if (queryList == null){
	    	queryList = new ArrayList<OMQuery>();
	    }
	 	return queryList;
	}
	
	 public void addNewDBQuery(OMQuery query) {
		// make sure it's not already here
		 if(query != null){
			 ArrayList<OMQuery> queryList = getRecentQueriesForDB();
			 queryList.add(query);
			 if(queryList.size() > 10)
				 queryList.remove(0);
			 saveConnection(queryList);
		 }
	}

	private void saveConnection(ArrayList<OMQuery> queryList) {
		data.setDataValue(KEY_GQ, queryList);
	}
	
	private ArrayList<OMQuery> getRecentQueriesForClass(String className)
	{
		if(className != null)
		{
			ArrayList<OMQuery> queryList = data.getDataValue(className);
		    if (queryList == null)
		    {
		    	queryList = new ArrayList<OMQuery>();
		    }
		 	return queryList;
		}
		return null;
	}
	
	 public void addNewQueryForClass(String className, OMQuery query)
	 { // make sure it's not already here
		 if(className != null)
		 {
			 ArrayList<OMQuery> queryList = getRecentQueriesForClass(className);
			 queryList.add(query);
			 if(queryList.size() > 5)
				 queryList.remove(0);
			 saveConnection(className, queryList);
		 }
	}

	private void saveConnection(String className, ArrayList<OMQuery> queryList) {
		data.setDataValue(className, queryList);
	}
}
