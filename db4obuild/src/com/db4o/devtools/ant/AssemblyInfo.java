package com.db4o.devtools.ant;

import java.util.*;

public class AssemblyInfo {
    
	public static final String PRODUCT = "db4o - database for objects";
    
    public static final String COPYRIGHT = "db4o 2005 - " + currentYear();

    public static final String COMPANY = "Versant Inc., San Mateo, CA, USA";

    private static int currentYear() {
    	return Calendar.getInstance(Locale.US).get(Calendar.YEAR);
	}
}