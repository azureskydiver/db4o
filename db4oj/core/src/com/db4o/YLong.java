/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


class YLong extends YapJavaClass
{
    
    private static final Long i_primitive = new Long(0);
    private static final Class i_class = i_primitive.getClass();
    
    
	static long decipher(String str){
		String str1 = "";
		for (int i = 0; i < str.length(); i+=2) {
			char[] chars = new char[2];
			chars[0] = str.charAt(i);
			chars[1] = str.charAt(i + 1);
			String str2 = new String(chars);
			str1 += (char)new Integer(str2).intValue();
		}
		return new Long(str1).longValue();
	}
	
	public int getID(){
		return 2;
	}
	
	public Class getJavaClass(){
		return i_class;
	}
	
	public Class getPrimitiveJavaClass(){
		return long.class;
	}
	
	public int linkLength(){
		return YapConst.YAPLONG_LENGTH;
	}
	
	Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(YapReader a_bytes){
		long ret = readLong(a_bytes);
		if(! Deploy.csharp){
			if(ret == Long.MAX_VALUE){
				return null;
			}
		}
		return new Long(ret);
	}
	
	static final long readLong(YapReader a_bytes){
		long l_return = 0;
		if (Deploy.debug){
			a_bytes.readBegin(YapConst.YAPLONG);
			if(Deploy.debugLong){
				l_return = new Long(new YapStringIO().read(a_bytes, YapConst.LONG_BYTES).trim()).longValue(); 
			}else{
				for (int i = 0; i < YapConst.LONG_BYTES; i++){
					l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
				}
			}
			a_bytes.readEnd();
		}else{
			for (int i = 0; i < YapConst.LONG_BYTES; i++){
				l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
			}
		}
		return l_return;
	}

	public void write(Object a_object, YapWriter a_bytes){
		if (! Deploy.csharp && a_object == null){
			writeLong(Long.MAX_VALUE,a_bytes);
		} else {
			writeLong(((Long)a_object).longValue(), a_bytes);
		}
	}
	
	static final void writeLong(long a_long, YapWriter a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(YapConst.YAPLONG);
			if(Deploy.debugLong){
				String l_s = "                                " + a_long;
				new YapStringIO().write(a_bytes, l_s.substring(l_s.length() - YapConst.LONG_BYTES));
			}
			else{
				for (int i = 0; i < YapConst.LONG_BYTES; i++){
					a_bytes._buffer[a_bytes._offset++] = (byte) (a_long >> ((YapConst.LONG_BYTES - 1 - i) * 8));
				}
			}
			a_bytes.writeEnd();
		}else{
			for (int i = 0; i < YapConst.LONG_BYTES; i++){
				a_bytes._buffer[a_bytes._offset++] = (byte) (a_long >> ((YapConst.LONG_BYTES - 1 - i) * 8));
			}
		}
	}	
	
	static final void writeLong(long a_long, byte[] bytes){
		for (int i = 0; i < YapConst.LONG_BYTES; i++){
			bytes[i] = (byte) (a_long >> ((YapConst.LONG_BYTES - 1 - i) * 8));
		}
	}	
	
	static final long readLong(YapWriter writer){
	    
        if (Deploy.debug) {
			long ret = 0;
            writer.readBegin(YapConst.YAPLONG);
            if (Deploy.debugLong) {
                ret = new Long(new YapStringIO().read(writer, YapConst.LONG_BYTES).trim())
                        .longValue();
            } else {
                for (int i = 0; i < YapConst.LONG_BYTES; i++) {
                    ret = (ret << 8) + (writer._buffer[i] & 0xff);
                }
                writer._offset += YapConst.LONG_BYTES;
            }
            writer.readEnd();
			return ret;
        }
		long l_return = 0;
		for (int i = 0; i < YapConst.LONG_BYTES; i++){
			l_return = (l_return << 8) + (writer._buffer[writer._offset++ ] & 0xff);
		}
		return l_return;
	}
	
		
	// Comparison_______________________
	
	protected long i_compareTo;
	
	long val(Object obj){
		return ((Long)obj).longValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
	
	boolean isEqual1(Object obj){
		return obj instanceof Long && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Long && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Long && val(obj) < i_compareTo;
	}
	
}
