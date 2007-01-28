/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.reflect.ReflectClass;


/**
 * @exclude
 */
public class YInt extends YapJavaClass {
    
    private static final Integer i_primitive = new Integer(0);
    
    public YInt(YapStream stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toInt(obj);
    }

    public Object defaultValue(){
		return i_primitive;
	}
	
    public int getID() {
        return 1;
    }

    protected Class primitiveJavaClass() {
        return int.class;
    }

    public int linkLength() {
        return YapConst.INT_LENGTH;
    }

    Object primitiveNull() {
        return i_primitive;
    }

    Object read1(YapReader a_bytes) {
        return new Integer(a_bytes.readInt());
    }

    static final int readInt(YapReader a_bytes) {
        if (Deploy.debug) {
			int ret = 0;
            a_bytes.readBegin(YapConst.YAPINTEGER);
            if (Deploy.debugLong) {
                ret =
                    Integer.valueOf(new YapStringIO().read(a_bytes, YapConst.INTEGER_BYTES).trim())
                        .intValue();
            } else {
                for (int i = 0; i < YapConst.INTEGER_BYTES; i++) {
                    ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
                }
            }
            a_bytes.readEnd();
			return ret;
        }
        return a_bytes.readInt();
    }

    public void write(Object obj, YapReader writer) {
        write(((Integer) obj).intValue(), writer);
    }

    public void write(int intValue, YapReader writer) {
        writeInt(intValue, writer);
    }

    static final void writeInt(int a_int, YapReader a_bytes) {
        if (Deploy.debug) {
            a_bytes.writeBegin(YapConst.YAPINTEGER);
            if (Deploy.debugLong) {
                String l_s = "                " + new Integer(a_int).toString();
                new YapStringIO().write(
                    a_bytes,
                    l_s.substring(l_s.length() - YapConst.INTEGER_BYTES));
            } else {
                for (int i = YapConst.WRITE_LOOP; i >= 0; i -= 8) {
                    a_bytes._buffer[a_bytes._offset++] = (byte) (a_int >> i);
                }
            }
            a_bytes.writeEnd();
        } else {
            a_bytes.writeInt(a_int);
        }
    }

    // Comparison_______________________

    private int i_compareTo;

    protected final int val(Object obj) {
        return ((Integer) obj).intValue();
    }
    
    public int compareTo(int other){
        return other - i_compareTo;
    }

    public void prepareComparison(int i) {
        i_compareTo = i;
    }
    
    void prepareComparison1(Object obj) {
        prepareComparison(val(obj));
    }
    
    public Object current1(){
        return new Integer(currentInt());
    }
    
    public int currentInt(){
        return i_compareTo;
    }

    boolean isEqual1(Object obj) {
        return obj instanceof Integer && val(obj) == i_compareTo;
    }

    boolean isGreater1(Object obj) {
        return obj instanceof Integer && val(obj) > i_compareTo;
    }

    boolean isSmaller1(Object obj) {
        return obj instanceof Integer && val(obj) < i_compareTo;
    }

    public void defragIndexEntry(ReaderPair readers) {
    	readers.incrementIntSize();
    }
}