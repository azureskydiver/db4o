/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;
import java.util.*;

import com.versant.trans.*;
import com.versant.util.*;

public class VodJvi {
	
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

}
