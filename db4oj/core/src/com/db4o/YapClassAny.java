/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;


/**
 * Undefined YapClass used for members of type Object.
 */
final class YapClassAny extends YapClass {
    
	public YapClassAny(YapStream stream){
		super(stream, stream.i_handlers.ICLASS_OBJECT);
	}

	public boolean canHold(ReflectClass claxx) {
		return true;
	}

	public void cascadeActivation(
		Transaction a_trans,
		Object a_object,
		int a_depth,
		boolean a_activate) {
		YapClass yc = forObject(a_trans, a_object, false);
		if (yc != null) {
			yc.cascadeActivation(a_trans, a_object, a_depth, a_activate);
		}
	}
    
	public void deleteEmbedded(MarshallerFamily mf, YapWriter reader) {
        mf._untyped.deleteEmbedded(reader);
	}
	
	public int getID() {
		return 11;
	}

	public boolean hasField(YapStream a_stream, String a_path) {
		return a_stream.classCollection().fieldExists(a_path);
	}
	
	public boolean hasIndex() {
	    return false;
	}
    
    public boolean hasFixedLength(){
        return false;
    }

	public boolean holdsAnyClass() {
		return true;
	}
    
    public int isSecondClass(){
        return YapConst.UNKNOWN;
    }
	
	boolean isStrongTyped(){
		return false;
	}
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        if(topLevel){
            header.addBaseLength(YapConst.INT_LENGTH); 
        }else{
            header.addPayLoadLength(YapConst.INT_LENGTH);  // single relink
        }
        YapClass yc = forObject(trans, obj, true);
        if( yc == null){
            return;
        }
        header.addPayLoadLength(YapConst.INT_LENGTH); //  type information int
        yc.calculateLengths(trans, header, false, obj, false);
    }
    
    public Object read(MarshallerFamily mf, YapWriter a_bytes, boolean redirect) throws CorruptionException{
        if(mf._untyped.useNormalClassRead()){
            return super.read(mf, a_bytes, redirect);
        }
        return mf._untyped.read(a_bytes);
    }

	public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, YapReader[] a_bytes) {
        return mf._untyped.readArrayHandler(a_trans, a_bytes);
	}
    
    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, YapReader reader, boolean toArray) throws CorruptionException{
        if(mf._untyped.useNormalClassRead()){
            return super.readQuery(trans, mf, withRedirection, reader, toArray);
        }
        return mf._untyped.readQuery(trans, reader, toArray);
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, YapReader reader, QCandidates candidates, boolean withIndirection) {
        if(mf._untyped.useNormalClassRead()){
            return super.readSubCandidate(mf, reader, candidates, withIndirection);
        }
        return mf._untyped.readSubCandidate(reader, candidates, withIndirection);
    } 
	
    public boolean supportsIndex() {
        return false;
    }
    
    public Object writeNew(MarshallerFamily mf, Object obj, boolean topLevel, YapWriter writer, boolean withIndirection, boolean restoreLinkeOffset) {
        return mf._untyped.writeNew(obj, restoreLinkeOffset, writer);
    }

    public void defrag(MarshallerFamily mf, ReaderPair readers) {
    	mf._untyped.defrag(readers);
    }
}
