/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.types;

import java.util.*;

public class RProperties extends RHashtable{
	public Object newInstance(){
		return new Properties();
	}
}
