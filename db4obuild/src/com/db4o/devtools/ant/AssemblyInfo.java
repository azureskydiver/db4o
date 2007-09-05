package com.db4o.devtools.ant;

import java.util.*;

public enum AssemblyInfo {
    DB4O("db4o", "db4o - database for objects"), DRS("dRS",
            "dRS - db4o Replication System");

    private static Calendar cal = Calendar.getInstance(Locale.US);

    public static final String COPYRIGHT = "db4o 2005 - "
            + cal.get(Calendar.YEAR);

    public static final String COMPANY = "db4objects Inc., San Mateo, CA, USA";

    private final String title;

    private final String product;

    AssemblyInfo(String title, String product) {
        this.title = title;
        this.product = product;
    }

    public String title() {
        return title;
    }

    public String product() {
        return product;
    }
}