/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.test2;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 */
public class RArrayList extends RCollection{
	public Object newInstance(){
		return new ArrayList();
	}
}
