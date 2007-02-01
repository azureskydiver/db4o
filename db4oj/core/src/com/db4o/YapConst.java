/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * @exclude 
 */
public final class YapConst
{
    static final Object initMe = init();

	public static final byte   YAPFILEVERSION		= 4;

	public static final byte	YAPBEGIN			= (byte)'{';
	public static final byte	YAPFILE				= (byte)'Y';
	static final byte	YAPID				= (byte)'#';
	static final byte	YAPPOINTER			= (byte)'>';
	public static final byte	YAPCLASSCOLLECTION	= (byte)'A';
	public static final byte	YAPCLASS			= (byte)'C';
	static final byte	YAPFIELD			= (byte)'F';
	public static final byte	YAPOBJECT			= (byte)'O';
	static final byte	YAPARRAY			= (byte)'N';
	static final byte	YAPARRAYN			= (byte)'Z';
	public static final byte	YAPINDEX			= (byte)'X';
	public static final byte	YAPSTRING			= (byte)'S';
	static final byte	YAPLONG				= (byte)'l';
	static final byte	YAPINTEGER			= (byte)'i';
	static final byte	YAPBOOLEAN			= (byte)'=';
	static final byte	YAPDOUBLE			= (byte)'d';
	static final byte	YAPBYTE				= (byte)'b';
	static final byte	YAPSHORT			= (byte)'s';
	static final byte	YAPCHAR				= (byte)'c';
	static final byte	YAPFLOAT			= (byte)'f';
	public static final byte	YAPEND				= (byte)'}';
	static final byte	YAPNULL				= (byte)'0';
    public static final byte   BTREE               = (byte)'T';               
    public static final byte   BTREE_NODE          = (byte)'B';               
    public static final byte   HEADER          = (byte)'H';               
	
	static final int	IDENTIFIER_LENGTH	= (Deploy.debug && Deploy.identifiers)?1:0;
	public static final int	BRACKETS_BYTES		= (Deploy.debug && Deploy.brackets)?1:0;
	static final int	BRACKETS_LENGTH		= BRACKETS_BYTES * 2;

	public static final int	LEADING_LENGTH		= IDENTIFIER_LENGTH + BRACKETS_BYTES;
	public static final int	ADDED_LENGTH		= IDENTIFIER_LENGTH + BRACKETS_LENGTH;

	static final int	SHORT_BYTES			= 2;
	static final int	INTEGER_BYTES		= (Deploy.debug && Deploy.debugLong)?11:4;
	static final int	LONG_BYTES			= (Deploy.debug && Deploy.debugLong)?20:8;
	static final int	CHAR_BYTES			= 2;

	static final int	UNSPECIFIED			= Integer.MIN_VALUE + 100; // make sure we don't fall over the -1 cliff

	public static final int	INT_LENGTH	= INTEGER_BYTES + ADDED_LENGTH;
	public static final int	ID_LENGTH		= INT_LENGTH;
	public static final int	LONG_LENGTH		= LONG_BYTES + ADDED_LENGTH;
	
	static final int	WRITE_LOOP			= (INTEGER_BYTES - 1) * 8;
	
	public static final int	OBJECT_LENGTH		= ADDED_LENGTH;

	public static final int	POINTER_LENGTH		= (INT_LENGTH * 2) +  ADDED_LENGTH;
	
	public static final int	MESSAGE_LENGTH 		= INT_LENGTH * 2 + 1;
	
	public static final byte   SYSTEM_TRANS        = (byte)'s';
	public static final byte   USER_TRANS          = (byte)'u';
	
	// debug constants
	static final byte XBYTE = (byte)'X';
	
	// TODO: This one is a terrible low-frequency blunder in YapArray.writeClass!!!
	// If YapClass-ID == 99999 (not very likely) then we will get IGNORE_ID. Change
	// to -Integer.MAX_VALUE or protect 99999 in YapFile.getPointerSlot() 
	public static final int IGNORE_ID = -99999;
	
	// This is a hard coded 2 Gig-Limit for YapClass-IDs.
    // TODO: get rid of magic numbers like this one
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
	public static final int LOCK_TIME_INTERVAL = 1000;
	static final int SERVER_SOCKET_TIMEOUT = Debug.longTimeOuts ? 1000000: 5000;  // jump out of the loop every 5 seconds
	static final int CLIENT_SOCKET_TIMEOUT = 300000;  // 5 minutes response time at the client side
	static final int CONNECTION_TIMEOUT = Debug.longTimeOuts ? 1000000: 180000;  // 1 minute until we start pinging dead or blocking clients
		
	// TODO: Consider to make configurable
    public static final int MAXIMUM_BLOCK_SIZE = 70000000; // 70 MB   
	static final int MAXIMUM_ARRAY_ENTRIES = 7000000; // 7 Million 
	static final int MAXIMUM_ARRAY_ENTRIES_PRIMITIVE = MAXIMUM_ARRAY_ENTRIES * 100; // 70 MB for byte arrays
	
    static Class CLASS_COMPARE;
    static Class CLASS_DB4OTYPE;
    static Class CLASS_DB4OTYPEIMPL;
    static Class CLASS_INTERNAL;
    static Class CLASS_UNVERSIONED;
    static Class CLASS_OBJECT;
    static Class CLASS_OBJECTCONTAINER;
    static Class CLASS_REPLICATIONRECORD; 
	static Class CLASS_STATICFIELD;
	static Class CLASS_STATICCLASS;
    static Class CLASS_TRANSIENTCLASS;
    
	public static final String EMBEDDED_CLIENT_USER = "embedded client";
	
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
    
    static final int UNCHECKED = 0;
    
    // Universal speaking variables.
    public static final int NO = -1;
    public static final int YES = 1;
    public static final int DEFAULT = 0;
    public static final int UNKNOWN = 0;
    
    public static final int OLD = -1;
    public static final int NEW = 1;

    
	public static final YapStringIOUnicode stringIO = new YapStringIOUnicode();
	
	private static final Object init(){
        CLASS_OBJECT = new Object().getClass();
        CLASS_COMPARE = com.db4o.config.Compare.class;
        CLASS_DB4OTYPE = com.db4o.types.Db4oType.class; 
        CLASS_DB4OTYPEIMPL = Db4oTypeImpl.class;
        CLASS_INTERNAL = Internal4.class;
        CLASS_UNVERSIONED = com.db4o.types.Unversioned.class;
        CLASS_OBJECTCONTAINER = ObjectContainer.class;
        CLASS_REPLICATIONRECORD = new ReplicationRecord().getClass();
        CLASS_STATICFIELD = new StaticField().getClass();
        CLASS_STATICCLASS = new StaticClass().getClass();
        CLASS_TRANSIENTCLASS = com.db4o.types.TransientClass.class;
		
	    return null;
	}
    
	// system classes that need to get loaded first
	static final Class[] ESSENTIAL_CLASSES = {
	// StaticClass should load Staticfield
	
	// TODO: remove unnecessary
	
        CLASS_STATICFIELD,
        CLASS_STATICCLASS
    };
	
    public static final String VIRTUAL_FIELD_PREFIX = "v4o";
    
    public static final int MAX_STACK_DEPTH = 20;


}
