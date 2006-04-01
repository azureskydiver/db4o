package com.db4o.foundation;

/**
 * @exclude
 */
public class KeySpecHashtable4 extends Hashtable4 {
	public KeySpecHashtable4(int a_size) {
		super(a_size);
	}
	
    public void put(KeySpec spec,byte value) {
    	put(spec,new Byte(value));
    }

    public void put(KeySpec spec,boolean value) {
    	put(spec,new Boolean(value));
    }

    public void put(KeySpec spec,int value) {
    	put(spec,new Integer(value));
    }

    public void put(KeySpec spec,Object value) {
    	super.put(spec,value);
    }

    public byte getAsByte(KeySpec spec) {
    	return ((Byte)get(spec)).byteValue();
    }

    public boolean getAsBoolean(KeySpec spec) {
    	return ((Boolean)get(spec)).booleanValue();
    }

    public int getAsInt(KeySpec spec) {
    	return ((Integer)get(spec)).intValue();
    }

    public String getAsString(KeySpec spec) {
    	return (String)get(spec);
    }

    public Object get(KeySpec spec) {
        Object value=super.get(spec);
        return (value==null ? spec.defaultValue() : value);
    }
}
