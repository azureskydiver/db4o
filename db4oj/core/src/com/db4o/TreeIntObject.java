/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;
/**
 * @exclude
 */
public class TreeIntObject extends TreeInt{
	
	public Object i_object;
	
	public TreeIntObject(int a_key){
		super(a_key);
	}
	
	public TreeIntObject(int a_key, Object a_object){
		super(a_key);
		i_object = a_object;
	}
	
	public Object read(YapReader a_bytes){
		int key = a_bytes.readInt();
		Object obj = null;
		if(i_object instanceof Tree){
			obj = new TreeReader(a_bytes, (Tree)i_object).read();
		}else{
			obj = ((Readable)i_object).read(a_bytes);	
		}
		return new TreeIntObject(key, obj);
	}
	
	public void write(YapReader a_writer){
		a_writer.writeInt(i_key);
		if(i_object == null){
			a_writer.writeInt(0);
		}else{
			if(i_object instanceof Tree){
				Tree.write(a_writer, (Tree)i_object);
			}else{
				((ReadWriteable)i_object).write(a_writer);
			}
		}
	}
	
	public int ownLength(){
		if(i_object == null){
			return YapConst.YAPINT_LENGTH * 2;
		}else{
			return YapConst.YAPINT_LENGTH + ((Readable)i_object).byteCount();
		}
	}
	
	boolean variableLength(){
		return true;
	}

}
