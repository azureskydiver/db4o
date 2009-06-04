package com.db4o.omplus.datalayer;

import java.util.*;

@SuppressWarnings("unchecked")
public class OMEData {
	
	HashMap<String, ArrayList> data;
	boolean isLastConnRemote;
	
	OMEData(){
		data = new HashMap<String, ArrayList>();
		isLastConnRemote = true;
	}
	
	public HashMap<String, ArrayList> getData() {
		return data;
	}

	public void setData(HashMap<String, ArrayList> data) {
		this.data = data;
	}
	
}
