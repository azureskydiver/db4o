/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;


abstract class YapJavaClass implements YapDataType {
    
    final YapStream _stream;
    
    public YapJavaClass(YapStream stream) {
        _stream = stream;
    }

    private boolean i_compareToIsNull;

    public void appendEmbedded3(YapWriter a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }

    public boolean canHold(Class a_class) {
        return getJavaClass() == a_class;
    }

    public void cascadeActivation(Transaction a_trans, Object a_object,
        int a_depth, boolean a_activate) {
        // do nothing
    }

    public void copyValue(Object a_from, Object a_to) {
        // do nothing
    }

    public void deleteEmbedded(YapWriter a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }

    public boolean equals(YapDataType a_dataType) {
        return (this == a_dataType);
    }

    public Class getJavaClass() {
        if (Deploy.csharp) {
            return primitiveNull().getClass();
        } else {
            if (Deploy.debug) {
                System.out
                    .println("YapJavaClass.getJavaClass should never be called.");
            }
            return null;
        }
    }

    public Class getPrimitiveJavaClass() {
        if (Deploy.csharp) {
            return getJavaClass();
        } else {
            if (Deploy.debug) {
                System.out
                    .println("YapJavaClass.getPrimitiveJavaClass should never be called.");
            }
            return null;
        }
    }

    public int getType() {
        return YapConst.TYPE_SIMPLE;
    }

    public YapClass getYapClass(YapStream a_stream) {
        return a_stream.i_handlers.i_yapClasses[getID() - 1];
    }

    public Object indexEntry(Object a_object) {
        return a_object;
    }

    public Object indexObject(Transaction a_trans, Object a_object) {
        return a_object;
    }

    public void prepareLastIoComparison(Transaction a_trans, Object obj) {
        prepareComparison(obj);
    }

    abstract Object primitiveNull();

    public YapDataType readArrayWrapper(Transaction a_trans, YapReader[] a_bytes) {
        // virtual and do nothing
        return null;
    }

    public Object readQuery(Transaction trans, YapReader reader, boolean toArray)
        throws CorruptionException {
        return read1(reader);
    }

    public Object read(YapWriter writer) throws CorruptionException {
        return read1(writer);
    }

    abstract Object read1(YapReader reader) throws CorruptionException;

    public void readCandidates(YapReader a_bytes, QCandidates a_candidates) {
        // do nothing
    }

    public Object readIndexEntry(YapReader a_reader) {
        try {
            return read1(a_reader);
        } catch (CorruptionException e) {
        }
        return null;
    }
    
    public Object readIndexObject(YapWriter a_writer) throws CorruptionException{
        return read(a_writer);
    }
    
    // FIXME: REFLECTOR This may be very slow and frequently used. Consider caching.
    public IClass classReflector(YapStream stream){
    	return stream.i_config.reflector().forClass(getJavaClass());
    }

    public boolean supportsIndex() {
        return true;
    }

    public abstract void write(Object a_object, YapWriter a_bytes);

    public void writeIndexEntry(YapWriter a_writer, Object a_object) {
        write(a_object, a_writer);
    }

    public void writeNew(Object a_object, YapWriter a_bytes) {
        if (Deploy.csharp) {
            if (a_object == null) {
                a_object = primitiveNull();
            }
        }
        write(a_object, a_bytes);
    }

    public YapComparable prepareComparison(Object obj) {
        if (obj == null) {
            i_compareToIsNull = true;
            return Null.INSTANCE;
        }
        i_compareToIsNull = false;
        prepareComparison1(obj);
        return this;
    }

    abstract void prepareComparison1(Object obj);

    public int compareTo(Object obj) {
        if (i_compareToIsNull) {
            if (obj == null) {
                return 0;
            }
            return 1;
        }
        if (obj == null) {
            return -1;
        }
        if (isEqual1(obj)) {
            return 0;
        }
        if (isGreater1(obj)) {
            return 1;
        }
        return -1;
    }

    public boolean isEqual(Object obj) {
        if (i_compareToIsNull) {
            return obj == null;
        }
        return isEqual1(obj);
    }

    abstract boolean isEqual1(Object obj);

    public boolean isGreater(Object obj) {
        if (i_compareToIsNull) {
            return obj != null;
        }
        return isGreater1(obj);
    }

    abstract boolean isGreater1(Object obj);

    public boolean isSmaller(Object obj) {
        if (i_compareToIsNull) {
            return false;
        }
        return isSmaller1(obj);
    }

    abstract boolean isSmaller1(Object obj);

}