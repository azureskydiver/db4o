/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.constructors;

class C2 {
	  private transient String x;
	  private String s;

	  private C2(String s) {
	    this.s=s;
	    this.x="x";
	  }

	  public String toString() {
	    return s+x.length();
	  }
	} 
