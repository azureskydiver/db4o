/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * YapString
 * Legacy rename for C# obfuscator production trouble
 */
final class YapString extends YapIndependantType {
    
    private YapStringIO i_stringIo; 
    private static final Class i_class = "".getClass();
    
    public void appendEmbedded3(YapWriter a_bytes) {
        YapWriter bytes = a_bytes.readEmbeddedObject();
        if (bytes != null) {
            a_bytes.addEmbedded(bytes);
        }
    }

    public boolean canHold(Class a_class) {
        return a_class == i_class;
    }

    public void cascadeActivation(
        Transaction a_trans,
        Object a_object,
        int a_depth,
        boolean a_activate) {
        // default: do nothing
    }

    static String cipher(long l) {
        String str1 = Long.toString(l);
        String str2 = "";
        for (int i = 0; i < str1.length(); i++) {
            str2 += str1.charAt(i);
        }
        return str2;
    }

    public boolean equals(YapDataType a_dataType) {
        return (this == a_dataType);
    }

    public int getID() {
        return 9;
    }

    byte getIdentifier() {
        return YapConst.YAPSTRING;
    }

    public Class getJavaClass() {
        return i_class;
    }

    public YapClass getYapClass(YapStream a_stream) {
        return a_stream.i_handlers.i_yapClasses[getID() - 1];
    }

    static String invert(String str) {
        StringBuffer buf = new StringBuffer();
        for (int i = str.length() - 1; i >= 0; i--) {
            buf.append(str.charAt(i));
        }
        return buf.toString();
    }
    
    public Object indexObject(Transaction a_trans, Object a_object){
        if(a_object != null){
	        int[] slot = (int[]) a_object;
	        return a_trans.i_stream.readObjectReaderByAddress(slot[0], slot[1]);
        }
        return null;
    }

    static String licenseEncrypt(String str) {
        str = str.toLowerCase();
        String ret = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = (char) (str.charAt(i) + ((char) i) + 1);
            ret += ch;
        }
        return ret;
    }

    public static long currentTimeMillis() {
        try {
            String str;
            if(Deploy.csharp){
                str = "metsySavaJ.gnal.o4j";
            }else{
                str = "metsyS.gnal.avaj";
            }
            return (
                (Long) Class
                    .forName(invert(str))
                    .getMethod(invert("silliMemiTtnerruc"), null)
                    .invoke(null, null))
                .longValue();
        } catch (Exception e) {
            return 2000000000000L;
        }
    }

    public Object read(YapWriter a_bytes) throws CorruptionException {
        i_lastIo = a_bytes.readEmbeddedObject();
        return read1(i_lastIo);
    }
    
    Object read1(YapReader bytes) throws CorruptionException {
		if (bytes == null) {
			return null;
		}
		if (Deploy.debug) {
			bytes.readBegin(0, YapConst.YAPSTRING);
		}
		String ret = readShort(bytes);
		if (Deploy.debug) {
			bytes.readEnd();
		}
		return ret;
    }
    
    public YapDataType readArrayWrapper(Transaction a_trans, YapReader[] a_bytes) {
        // virtual and do nothing
        return null;
    }

    public void readCandidates(YapReader a_bytes, QCandidates a_candidates) {
        // do nothing
    }

    public Object readIndexEntry(YapReader a_reader) {
        return new int[] {a_reader.readInt(), a_reader.readInt()};
    }

	public Object readQuery(Transaction a_trans, YapReader a_reader, boolean a_toArray) throws CorruptionException{
	    YapReader reader = a_reader.readEmbeddedObject(a_trans);
	    if(a_toArray) {
	        if(reader != null) {
	            return reader.toString(a_trans);
	        }
	    }
	    return reader;
	}
	
    final String readShort(YapReader a_bytes) throws CorruptionException {
        int length = a_bytes.readInt();
        if (length > YapConst.MAXIMUM_BLOCK_SIZE) {
            if (Debug.atHome) {
                throw new CorruptionException();
            }
            throw new CorruptionException();
        }
        if (length > 0) {
            return i_stringIo.read(a_bytes, length);
        }
        return "";
    }
    
    void setStringIo(YapStringIO a_io) {
        i_stringIo = a_io;
    }
    
    public boolean supportsIndex() {
        return true;
    }

    public void writeIndexEntry(YapWriter a_writer, Object a_object) {
        if(a_object == null){
            a_writer.writeInt(0);
            a_writer.writeInt(0);
        }else{
            int[] slot = (int[])a_object;
            a_writer.writeInt(slot[0]);
            a_writer.writeInt(slot[1]);
        }
    }
    
    public void writeNew(Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
        } else {
            String str = (String) a_object;
            int length = i_stringIo.length(str);
            YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
            if (Deploy.debug) {
                bytes.writeBegin(YapConst.YAPSTRING, length);
            }
            bytes.writeInt(str.length());
            i_stringIo.write(bytes, str);
            if (Deploy.debug) {
                bytes.writeEnd();
            }
            bytes.setID(a_bytes.i_offset);
            i_lastIo = bytes;
            a_bytes.getStream().writeEmbedded(a_bytes, bytes);
            a_bytes.incrementOffset(YapConst.YAPID_LENGTH);
            a_bytes.writeInt(length);
        }
    }

    final void writeShort(String a_string, YapReader a_bytes) {
        if (a_string == null) {
            a_bytes.writeInt(0);
        } else {
            a_bytes.writeInt(a_string.length());
            i_stringIo.write(a_bytes, a_string);
        }
    }

    static String fromIntArray(int[] ints) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < ints.length; i++) {
            buf.append((char) ints[i]);
        }
        return buf.toString();
    }

    public int getType() {
        return YapConst.TYPE_SIMPLE;
    }


    // Comparison_______________________

    private YapReader i_compareTo;

    private YapReader val(Object obj) {
        if(obj instanceof YapReader) {
            return (YapReader)obj;
        }
        if(obj instanceof String) {
            String str = (String)obj;
            YapReader reader = new YapReader(i_stringIo.length(str));
            if(Deploy.debug) {
                reader.writeBegin(YapConst.YAPSTRING, i_stringIo.length(str));
            }
            writeShort(str, reader);
            if(Deploy.debug) {
                reader.writeEnd();
            }
            return reader;
        }
        return null;
    }
    
	public void prepareLastIoComparison(Transaction a_trans, Object obj) {
	    if(obj == null) {
	        i_compareTo = null;    
	    }else {
	        i_compareTo = i_lastIo;
	    }
	}

    public YapComparable prepareComparison(Object obj) {
        if (obj == null) {
            i_compareTo = null;
            return Null.INSTANCE;
        }
        i_compareTo = val(obj);
        return this;
    }
    
    public int compareTo(Object obj) {
        if(i_compareTo == null) {
            if(obj == null) {
                return 0;
            }
            return 1;
        }
        return compare(i_compareTo, val(obj));
    }

    public boolean isEqual(Object obj) {
        if(i_compareTo == null){
            return obj == null;
        }
        return i_compareTo.containsTheSame(val(obj));
    }

    public boolean isGreater(Object obj) {
        if(i_compareTo == null){
            // this should be called for indexing only
            // object is always greater
            return obj != null;
        }
        return compare(i_compareTo, val(obj)) > 0;
    }

    public boolean isSmaller(Object obj) {
        if(i_compareTo == null){
            // this should be called for indexing only
            // object is always greater
            return false;
        }
        return compare(i_compareTo, val(obj)) < 0;
    }

    /** 
     * returns: -x for left is greater and +x for right is greater 
     *
     * TODO: You will need collators here for different languages.  
     */
    final int compare(YapReader a_compare, YapReader a_with) {
        if (a_compare == null) {
            if (a_with == null) {
                return 0;
            }
            return 1;
        }
        if (a_with == null) {
            return -1;
        }
        return compare(a_compare.i_bytes, a_with.i_bytes);
    }
    
    static final int compare(byte[] compare, byte[] with){
        int min = compare.length < with.length ? compare.length : with.length;
        int start = YapConst.YAPINT_LENGTH;
        if(Deploy.debug) {
            start += YapConst.LEADING_LENGTH;
            min -= YapConst.BRACKETS_BYTES;
        }
        for(int i = start;i < min;i++) {
            if (compare[i] != with[i]) {
                return with[i] - compare[i];
            }
            
        }
        return with.length - compare.length;
    }

}
