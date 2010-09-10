/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;
import java.util.*;

import com.db4o.drs.inside.*;
import com.versant.trans.*;
import com.versant.util.*;

public class VodJvi {
	
	private static final int MAX_DB_NAME_LENGTH = 31;

	private static final String VEDSECHN_SCHEMA = "/lib/vedsechn.sch";

	private static final String CHANNEL_SCHEMA = "/lib/channel.sch";

	private final VodDatabase _vod;

	public VodJvi(VodDatabase vod) {
		_vod = vod;
	}

	public void close() {
		
	}

	public String versantRootPath() {
		return DBUtility.versantRootPath();
	}
	
	private void defineSchema(String schema) {
		DBUtility.defineSchema(_vod.databaseName(), new File(new File(versantRootPath()), schema).getAbsolutePath());
	}
	
	public void createEventSchema() {
		defineSchema(CHANNEL_SCHEMA);
		defineSchema(VEDSECHN_SCHEMA);
	}
	
	public TransSession createTransSession() {
        Properties properties = new Properties ();
        properties.put ("database", _vod.databaseName());
        properties.put ("lockmode", com.versant.fund.Constants.NOLOCK + "");
        properties.put ("options",  com.versant.fund.Constants.READ_ACCESS + "");
        return new TransSession(properties);
	}
	
	public short newDbId(String databaseName){
		int dbid = invokeDbId(databaseName, "-c");
		if(DrsDebug.verbose){
			System.out.println("dbid " + dbid + " created for '" + databaseName + "'");
		}
		return (short) dbid;
	}

	public void deleteDbId(String databaseName) {
		int dbid = invokeDbId(databaseName, "-d");
		if(DrsDebug.verbose){
			System.out.println("dbid " + dbid + " deleted for '" + databaseName + "'");
		}
	}

	private int invokeDbId(String databaseName, String arg) {
		Properties props = new Properties();
		props.put(arg, "");
		int dbid = com.versant.util.DBUtility.dbid(databaseName,props);
		return dbid;
	}

	public int dbIdFor(String databaseName) {
		String node = DBUtility.dbidNode();
		String fullDatabaseName = databaseName + "@" + node;
		DBListInfo[] dbList = DBUtility.dbList(node);
		for (DBListInfo info : dbList) {
			if(info.getDBName().startsWith(databaseName + "@")) {
				return info.getDBID();
			}
		}
		throw new IllegalArgumentException("Unknown database: " + fullDatabaseName);
	}
	
	public static String safeDatabaseName(String databaseName) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < databaseName.length(); i++) {
			char c = databaseName.charAt(i);
			if(Character.isLetterOrDigit(c)){
				sb.append(c);
			}
		}
		if(sb.length() > MAX_DB_NAME_LENGTH) {
			sb.delete(0, sb.length() - MAX_DB_NAME_LENGTH);
		}
		return sb.toString();
	}

}
