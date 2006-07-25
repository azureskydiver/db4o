package com.db4odoc.f1.diagnostics;

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
