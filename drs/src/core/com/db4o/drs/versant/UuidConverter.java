/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.foundation.*;

/**
 * Db4oUuid long part format:
 * 
 * | 1    |  6         | 42        | 9        | 6      | 
 * |unused| timestamp              | counter           |  
 * |      | zeroes     | used      | zeroes   | used   |    
 * 
 * Vod loid:
 * 
 * | 16   | 42         | 6          |
 * | dbid | timestamp  | counter    |
 * 
 * 
 * Conversion idea: Take the 6 bit from the timestamp and the 9 bits from the counter that are assumed to be unused in db4o and use them for the dbid on vod side. 
 * 
 */
public class UuidConverter {
	
	public static long vodLoidFrom(long databaseId, long db4oLongPart) {
		long vodObjectIdPart = TimeStampIdGenerator.convert64BitIdTo48BitId(db4oLongPart);
		return (databaseId << 48) | vodObjectIdPart;
	}
	
	public static long longPartFromVod(long vodId){
		return longPartFromVod(objectId1(vodId), objectId2(vodId));
	}
	
	public static long longPartFromVod(long objectId1, long objectId2){
		long vodObjectId = ((long)objectId1 << 32  | objectId2)  & 0x0000FFFFFFFFFFFFL;
		
		return TimeStampIdGenerator.convert48BitIdTo64BitId(vodObjectId);
	}
	
    public static int databaseId(long value) {
        return (int)(value >>> 48);
    }

    public static int objectId1(long value) {
        return (int)(value >> 32) & 0xFFFF;
    }

    public static long objectId2(long value) {
    	return value & 0x00000000FFFFFFFFL;
    }

}
