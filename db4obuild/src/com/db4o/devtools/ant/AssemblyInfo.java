package com.db4o.devtools.ant;

import java.util.*;

public class AssemblyInfo {
    private static Calendar cal = Calendar.getInstance(Locale.US);

    public static final String COPYRIGHT = "db4o 2005 - "
            + cal.get(Calendar.YEAR);

    public static final String COMPANY = "db4objects Inc., San Mateo, CA, USA";

    private final String title;

    private final String product;

    public AssemblyInfo(String title, String product) {
        this.title = title;
        this.product = product;
    }

    public static AssemblyInfo DB4O() {
        return new AssemblyInfo("db4o", "db4o - database for objects");
    }

    public String title() {
        return title;
    }

    public String product() {
        return product;
    }
}