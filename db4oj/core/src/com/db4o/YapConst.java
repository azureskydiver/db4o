/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

final class YapConst
{
    static final Object initMe = init();
    
	static final byte	YAPBEGIN			= (byte)'{';
	static final byte	YAPFILE				= (byte)'Y';
	static final byte	YAPID				= (byte)'#';
	static final byte	YAPPOINTER			= (byte)'>';
	static final byte	YAPCLASSCOLLECTION	= (byte)'A';
	static final byte	YAPCLASS			= (byte)'C';
	static final byte	YAPFIELD			= (byte)'F';
	static final byte	YAPOBJECT			= (byte)'O';
	static final byte	YAPARRAY			= (byte)'N';
	static final byte	YAPARRAYN			= (byte)'Z';
	static final byte	YAPINDEX			= (byte)'X';
	static final byte	YAPSTRING			= (byte)'S';
	static final byte	YAPLONG				= (byte)'l';
	static final byte	YAPINTEGER			= (byte)'i';
	static final byte	YAPBOOLEAN			= (byte)'=';
	static final byte	YAPDOUBLE			= (byte)'d';
	static final byte	YAPBYTE				= (byte)'b';
	static final byte	YAPSHORT			= (byte)'s';
	static final byte	YAPCHAR				= (byte)'c';
	static final byte	YAPFLOAT			= (byte)'f';
	static final byte	YAPEND				= (byte)'}';
	static final byte	YAPNULL				= (byte)'0';
	
	static int[] system = new int[]{106,97,118,97,46,108,97,110,
		103,46,83,121,115,116,101,109};
	static int[] currentTimeMillis = new int[]{99, 117, 114, 114,
		101,110,116,84,105,109,101,77,105,108,108,105,115};
	static int[] dateTime = new int[]{83, 121, 115, 116, 101, 109, 46, 68, 97, 116, 101, 84, 105, 109, 101};
	static int[] now = new int[]{78, 111, 119};
	static int[] ticks = new int[] {84, 105, 99, 107, 115};
	static int[] exit = new int[]{101, 120, 105, 116};
	
	static final int	IDENTIFIER_LENGTH	= (Deploy.debug && Deploy.identifiers)?1:0;
	static final int	BRACKETS_BYTES		= (Deploy.debug && Deploy.brackets)?1:0;
	static final int	BRACKETS_LENGTH		= BRACKETS_BYTES * 2;

	static final int	LEADING_LENGTH		= IDENTIFIER_LENGTH + BRACKETS_BYTES;
	static final int	ADDED_LENGTH		= IDENTIFIER_LENGTH + BRACKETS_LENGTH;

	static final int	SHORT_BYTES			= 2;
	static final int	INTEGER_BYTES		= (Deploy.debug && Deploy.debugLong)?11:4;
	static final int	LONG_BYTES			= (Deploy.debug && Deploy.debugLong)?20:8;
	static final int	CHAR_BYTES			= 2;

	static final int	UNSPECIFIED			= Integer.MIN_VALUE + 100; // make sure we don't fall over the -1 cliff

	static final int	YAPINT_LENGTH		= INTEGER_BYTES + ADDED_LENGTH;
	static final int	YAPID_LENGTH		= YAPINT_LENGTH;
	static final int	YAPLONG_LENGTH		= LONG_BYTES + ADDED_LENGTH;
	
	static final int	WRITE_LOOP			= (INTEGER_BYTES - 1) * 8;
	
	static final int	OBJECT_LENGTH		= ADDED_LENGTH;

	static final int	POINTER_LENGTH		= (YAPINT_LENGTH * 2) +  ADDED_LENGTH;
	
	static final int	MESSAGE_LENGTH 		= YAPINT_LENGTH * 2 + 1;
	
	static final byte   SYSTEM_TRANS        = (byte)'s';
	static final byte   USER_TRANS          = (byte)'u';
	
	// debug constants
	static final byte XBYTE = (byte)'X';
	
	// TODO: This one is a terrible low-frequency blunder in YapArray.writeClass!!!
	// If YapClass-ID == 99999 (not very likely) then we will get IGNORE_ID. Change
	// to -Integer.MAX_VALUE or protect 99999 in YapFile.getPointerSlot() 
	static final int IGNORE_ID = -99999;
	
	// This is a hard coded 2 Gig-Limit for YapClass-IDs.
	static final int PRIMITIVE = -2000000000;
	
	// optimized type information
	static final int TYPE_SIMPLE 			= 1;
	static final int TYPE_CLASS 			= 2;
	static final int TYPE_ARRAY 			= 3;
	static final int TYPE_NARRAY 			= 4;
	
	// message levels
	static final int	NONE = 0;  // Use if > NONE: normal messages
	static final int	STATE = 1; // if > STATE: state messages
	static final int	ACTIVATION = 2; // if > ACTIVATION: activation messages
	
	static final int    TRANSIENT = -1;
	static final int    ADD_MEMBERS_TO_ID_TREE_ONLY = 0;
	static final int    ADD_TO_ID_TREE = 1;
	
	// String Encoding
	static final byte	ISO8859 = (byte)1;
	static final byte	UNICODE = (byte)2;

	// Timings
	static final int LOCK_TIME_INTERVAL = 1000;
	static final int SERVER_SOCKET_TIMEOUT = Debug.longTimeOuts ? 1000000: 5000;  // jump out of the loop every 5 seconds
	static final int CLIENT_SOCKET_TIMEOUT = 300000;  // 5 minutes response time at the client side
	static final int CONNECTION_TIMEOUT = Debug.longTimeOuts ? 1000000: 180000;  // 1 minute until we start pinging dead or blocking clients
	
	// C/S tuning paramaters
	static final int PREFETCH_ID_COUNT = 10;
	static final int PREFETCH_OBJECT_COUNT = 10;
	
	// chaos fixery
	static final int MAXIMUM_BLOCK_SIZE = 70000000; // 70 MB 
	static final int MAXIMUM_ARRAY_ENTRIES = 7000000; // 7 Million 
	static final int MAXIMUM_ARRAY_ENTRIES_PRIMITIVE = MAXIMUM_ARRAY_ENTRIES * 100; // 70 MB for byte arrays
	
	// hidden and unused license key to identify possible hacks
	// static final String lic = Lic.lic;
	
	static Class CLASS_DB4OTYPEIMPL;
	static Class CLASS_OBJECT;
	static Class CLASS_DB4ODATABASE;
	static Class CLASS_INTERNAL;
	static Class CLASS_TRANSIENTCLASS;
	static Class CLASS_COMPARE;
	static Class CLASS_DB4OTYPE;
	static Class CLASS_METAINDEX;
	static Class CLASS_METAFIELD;
	static Class CLASS_METACLASS;
	static Class CLASS_STATICFIELD;
	static Class CLASS_STATICCLASS;
	static Class CLASS_PBOOTRECORD;
	static Class CLASS_REPLICATIONRECORD;
	static Class CLASS_OBJECTCONTAINER;
	static Class CLASS_CLASS;
	
	static final String EMBEDDED_CLIENT_USER = "embedded client";
	
	// bits in YapMeta.i_state
	// and reuse in other classes 
    static final int CLEAN = 0;
    static final int ACTIVE = 1;
    static final int PROCESSING = 2;
    static final int CACHED_DIRTY = 3;
    static final int CONTINUE = 4;
    static final int STATIC_FIELDS_STORED = 5;
    static final int CHECKED_CHANGES = 6;
    static final int DEAD = 7;
    static final int READING = 8;
    
	public static final YapStringIOUnicode stringIO = new YapStringIOUnicode();
	
	public static RuntimeException virtualException(){
		return new RuntimeException();
	}
	
	private static final Object init(){
        CLASS_OBJECT = new Object().getClass();
        CLASS_DB4OTYPEIMPL = db4oClass("Db4oTypeImpl"); 
        CLASS_DB4ODATABASE = new Db4oDatabase().getClass(); 
        CLASS_INTERNAL = db4oClass("Internal");
        CLASS_TRANSIENTCLASS = db4oClass("types.TransientClass");
        CLASS_COMPARE = db4oClass("config.Compare");
        CLASS_DB4OTYPE = db4oClass("types.Db4oType"); 
        CLASS_METAINDEX = new MetaIndex().getClass();
        CLASS_METAFIELD = new MetaField().getClass();
        CLASS_METACLASS = new MetaClass().getClass();
        CLASS_STATICFIELD = new StaticField().getClass();
        CLASS_STATICCLASS = new StaticClass().getClass();
        CLASS_PBOOTRECORD = new PBootRecord().getClass();
        CLASS_REPLICATIONRECORD = new ReplicationRecord().getClass();
        CLASS_OBJECTCONTAINER = db4oClass("ObjectContainer");
        CLASS_CLASS = CLASS_OBJECT.getClass();
	    return null;
	}
	
	private static final Class db4oClass(String name){
	    try{
	        return Class.forName("com.db4o." + name);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return null;
	}
	
	// system classes that need to get loaded first
	static final Class[] ESSENTIAL_CLASSES = {
	// MetaClass should load the other two Meta
	// StaticClass should load Staticfield
	
	// TODO: remove unnecessary
	
	// TODO: improved approach would use interface to autodetect
	
	
	    CLASS_METAINDEX,
	    CLASS_METAFIELD,
        CLASS_METACLASS,
        CLASS_STATICFIELD,
        CLASS_STATICCLASS
    };
	
}
