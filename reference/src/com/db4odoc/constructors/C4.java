/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.constructors;

class C4 {
	  private String s;
	  private transient int i;

	  private C4(String s) {
	    this.s=s;
	    this.i=s.length();
	  }

	  public String toString() {
	    return s+i;
	  }
	} 
