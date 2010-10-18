/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.foundation.*;

public class UuidConverter {
	
	public static long vodLoidFrom(long databaseId, long db4oLongPart) {
		long vodObjectIdPart = TimeStampIdGenerator.convert64BitIdTo48BitId(db4oLongPart);
		return (databaseId << 48) | vodObjectIdPart;
	}
	
	public static long longPartFromVod(long vodId){
		return longPartFromVod(databaseId(vodId), objectId1(vodId), objectId2(vodId));
	}
	
	public static long longPartFromVod(long databaseId, long objectId1, long objectId2){
		long vodObjectId = (long)objectId1 << 32  | objectId2;
		System.err.println(Long.toHexString(vodObjectId));
		long shifted = vodObjectId << 9;
		shifted = shifted >>> 9;
		System.err.println(Long.toHexString(shifted));
	    
		return TimeStampIdGenerator.convert48BitIdTo64BitId(shifted);
	}
	
    public static int databaseId(long value) {
        return (int)(value >> 48);
    }

    public static int objectId1(long value) {
        return (int)(value >> 32) & 0xFFFF;
    }

    public static long objectId2(long value) {
        return (long)((value << 32) >> 32);
    }

	


}
