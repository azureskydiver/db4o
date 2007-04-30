/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos.circuits.magnycours;

import org.polepos.framework.*;


public class MagnycoursItem  implements CheckSummable{
	
	public int workLoad;
	
	public MagnycoursItem(){
		
	}
	
	public MagnycoursItem(int workLoad_) {
		workLoad = workLoad_;
	}

	public long checkSum() {
		return workLoad;
	}

}
