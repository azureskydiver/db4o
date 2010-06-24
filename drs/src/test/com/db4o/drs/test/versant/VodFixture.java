/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.versant.util.*;

public class VodFixture implements DrsFixture{
	
	private boolean _dbCreated;
	
	private final String _name;

	public VodFixture(String name){
		_name = name;
	}
	
	
	
	
	
	/**
	 * 
	 * TODO: All code below is in VodDatabase. Delegate there.
	 * 
	 * 
	 */
	
	
	
	
	
	
	private boolean dbExists(){
		DBListInfo[] dbList = DBUtility.dbList();
		for (DBListInfo dbListInfo : dbList) {
			String name = dbListInfo.getDBName();
			int indexOfHostName = name.indexOf("@");
			if(indexOfHostName >= 0){
				name = name.substring(0, indexOfHostName);
			}
			if(_name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public void clean() {
		if(! dbExists()){
			_dbCreated = false;
			return;
		}
		Properties props = new Properties();
		props.put ("-f","");
		try{
			DBUtility.stopDB(_name,props);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		props.put("-rmdir","");
		try{
			DBUtility.removeDB(_name, props);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		_dbCreated = false;
	}
	
	private void createDb(){
		Properties p=new Properties();
		DBUtility.makeDB(_name,p);
		DBUtility.createDB(_name);
		_dbCreated = true;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void open() {
		if(!_dbCreated){
			createDb();
		}
		
	}

	public TestableReplicationProviderInside provider() {
		// TODO Auto-generated method stub
		return null;
	}

}
