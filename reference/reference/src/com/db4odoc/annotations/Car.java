/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.annotations;

import com.db4o.config.annotations.*;


public class Car {
	@Indexed
    private String model;
    private int year;
}
