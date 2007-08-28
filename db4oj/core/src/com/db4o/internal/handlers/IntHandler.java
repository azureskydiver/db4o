/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.CorruptionException;
import com.db4o.Deploy;
import com.db4o.foundation.Coercion4;
import com.db4o.internal.Buffer;
import com.db4o.internal.BufferPair;
import com.db4o.internal.Const4;
import com.db4o.internal.LatinStringIO;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.internal.StatefulBuffer;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
import com.db4o.reflect.ReflectClass;

/**
 * @exclude
 */
public class IntHandler extends PrimitiveHandler {
    
    private static final Integer i_primitive = new Integer(0);
    
    public IntHandler(ObjectContainerBase stream) {
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
        return Const4.INT_LENGTH;
    }

    public Object primitiveNull() {
        return i_primitive;
    }
    
    public Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect) throws CorruptionException {
        return mf._primitive.readInteger(writer);
    }

    Object read1(Buffer a_bytes) {
        return new Integer(a_bytes.readInt());
    }    

    public void write(Object obj, Buffer writer) {
        write(((Integer) obj).intValue(), writer);
    }

    public void write(int intValue, Buffer writer) {
        writeInt(intValue, writer);
    }

    public static final void writeInt(int a_int, Buffer a_bytes) {
        if (Deploy.debug) {
            a_bytes.writeBegin(Const4.YAPINTEGER);
            if (Deploy.debugLong) {
                String l_s = "                " + new Integer(a_int).toString();
                new LatinStringIO().write(
                    a_bytes,
                    l_s.substring(l_s.length() - Const4.INTEGER_BYTES));
            } else {
                for (int i = Const4.WRITE_LOOP; i >= 0; i -= 8) {
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

    public void defragIndexEntry(BufferPair readers) {
    	readers.incrementIntSize();
    }
    
    public Object read(ReadContext context) {
        return new Integer(context.readInt());
    }

    public void write(WriteContext context, Object obj) {
        context.writeInt(((Integer) obj).intValue());
    }
}