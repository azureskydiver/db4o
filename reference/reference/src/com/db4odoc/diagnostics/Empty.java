/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.diagnostics;

import java.util.Calendar;
import java.text.DateFormat;


public class Empty {    
    
    public Empty() {
    }
        
	public String CurrentTime()
	{
		Calendar cl = Calendar.getInstance();
		DateFormat df = DateFormat.getDateTimeInstance();
		String time = df.format(cl.getTime());
		return time;
	}

	public String ToString()
	{
		return CurrentTime();
	}
}
