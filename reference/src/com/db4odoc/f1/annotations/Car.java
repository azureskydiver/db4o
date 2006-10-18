/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.annotations;

import com.db4o.config.annotations.Indexed;


public class Car {
	@Indexed
    private String model;
    private int year;
}
