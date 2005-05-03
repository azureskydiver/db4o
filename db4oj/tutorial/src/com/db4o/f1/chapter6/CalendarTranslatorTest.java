/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.f1.chapter6;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class CalendarTranslatorTest {
    private static final String YAPFILENAME = "caltrans.yap";

    public static void main(String[] args) {
        Db4o.configure().callConstructors(true);
        Db4o.configure().objectClass(GregorianCalendar.class).translate(new CalendarTranslator());
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date(1000));
            db.set(cal);
            db.commit();
            db.close();
            cal=null;
            db=Db4o.openFile(YAPFILENAME);
            ObjectSet result=db.get(Calendar.class);
            System.out.println(result.next());
        }
        finally {
            db.close();
        }
    }
}
