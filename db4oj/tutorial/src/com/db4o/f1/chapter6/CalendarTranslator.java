/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.f1.chapter6;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

public class CalendarTranslator implements ObjectConstructor {
//    private class StoredCalendar {
//        private long time;
//        private String zone;
//
//        public StoredCalendar(long time, String zone) {
//            this.time = time;
//            this.zone = zone;
//        }        
//    }
    
    public Object onStore(ObjectContainer container, Object applicationObject) {
        Calendar cal=(Calendar)applicationObject;
        long time=cal.getTime().getTime();
        String zone=cal.getTimeZone().getID();
        return new Object[] {new Long(time),zone};
    }

    public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
    }

    public Class storedClass() {
        return Object[].class;
    }

    public Object onInstantiate(ObjectContainer container, Object storedObject) {
        Object[] storedcal=(Object[])storedObject;
        TimeZone zone=TimeZone.getTimeZone((String)storedcal[1]);
        Calendar cal=Calendar.getInstance(zone);
        cal.setTime(new Date(((Long)storedcal[0]).longValue()));
        return cal;
    }
}
