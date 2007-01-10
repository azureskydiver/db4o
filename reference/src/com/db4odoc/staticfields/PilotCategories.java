/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.staticfields;


public class  PilotCategories {
	private String qualification = null;
	public final static PilotCategories WINNER=new PilotCategories("WINNER");
	public final static PilotCategories TALENTED=new PilotCategories("TALENTED");
	public final static PilotCategories AVERAGE=new PilotCategories("AVERAGE");
	public final static PilotCategories DISQUALIFIED=new PilotCategories("DISQUALIFIED");
	
	private PilotCategories(String qualification){
		this.qualification = qualification;
	}
	
	public PilotCategories(){
		
	}
	
	public void testChange(String qualification){
		this.qualification = qualification;
	}
    public String toString() {
        return qualification;
    }

}
