/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.*;
import com.db4o.inside.marshall.*;
import com.db4o.inside.slots.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class YapArray extends YapIndependantType {
	
	public final YapStream _stream;
    public final TypeHandler4 i_handler;
    public final boolean i_isPrimitive;
    public final ReflectArray _reflectArray;

    public YapArray(YapStream stream, TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(stream);
    	_stream = stream;
        i_handler = a_handler;
        i_isPrimitive = a_isPrimitive;
        _reflectArray = stream.reflector().array();
    }

    public Object[] allElements(Object a_object) {
        Object[] all = new Object[_reflectArray.getLength(a_object)];
        for (int i = all.length - 1; i >= 0; i--) {
            all[i] = _reflectArray.get(a_object, i);
        }
        return all;
    }

    public boolean canHold(ReflectClass claxx) {
        return i_handler.canHold(claxx);
    }

    public final void cascadeActivation(
        Transaction a_trans,
        Object a_object,
        int a_depth,
        boolean a_activate) {
        // We simply activate all Objects here
        if (i_handler instanceof YapClass) {
            
            a_depth --;
            
            Object[] all = allElements(a_object);
            if (a_activate) {
                for (int i = all.length - 1; i >= 0; i--) {
                    _stream.stillToActivate(all[i], a_depth);
                }
            } else {
                for (int i = all.length - 1; i >= 0; i--) {
                  _stream.stillToDeactivate(all[i], a_depth, false);
                }
            }
        }
    }
    
    public ReflectClass classReflector(){
    	return i_handler.classReflector();
    }

    final TreeInt collectIDs(MarshallerFamily mf, TreeInt tree, YapWriter reader){
        return mf._array.collectIDs(this, tree, reader);
    }
    
    public final TreeInt collectIDs1(Transaction trans, TreeInt tree, YapReader reader){
        if (reader != null) {
            if (Deploy.debug) {
                reader.readBegin(identifier());
            }
            int count = elementCount(trans, reader);
            for (int i = 0; i < count; i++) {
                tree = (TreeInt)Tree.add(tree, new TreeInt(reader.readInt()));
            }
        }
        return tree;
    }
    
    public Object comparableObject(Transaction a_trans, Object a_object){
        throw Exceptions4.virtualException();
    }

    public final void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes) {
        mf._array.deleteEmbedded(this, a_bytes);
    }

    
    // FIXME: This code has not been called in any test case when the 
    //        new ArrayMarshaller was written.
    //        Apparently it only frees slots.
    //        For now the code simply returns without freeing.
    public final void deletePrimitiveEmbedded(
        
        YapWriter a_bytes,
        YapClassPrimitive a_classPrimitive) {
        
        int address = a_bytes.readInt();
        int length = a_bytes.readInt();
        

        if(true){
            return;
        }
        
        
        if (address > 0) {
            Transaction trans = a_bytes.getTransaction();
            YapWriter bytes =
                a_bytes.getStream().readObjectWriterByAddress(trans, address, length);
            if (bytes != null) {
                if (Deploy.debug) {
                    bytes.readBegin(identifier());
                }
                for (int i = elementCount(trans, bytes); i > 0; i--) {
                    int id = bytes.readInt();
                    Slot slot = trans.getSlotInformation(id);
                    
					a_classPrimitive.free(trans, id, slot._address,slot._length);
                }
            }
            
            trans.slotFreeOnCommit(address, address, length);
        }
    }

    public int elementCount(Transaction a_trans, YapReader a_bytes) {
        int typeOrLength = a_bytes.readInt();
        if (typeOrLength >= 0) {
            return typeOrLength;
        }
        return a_bytes.readInt();
    }

    public final boolean equals(TypeHandler4 a_dataType) {
        if (a_dataType instanceof YapArray) {
            if (((YapArray) a_dataType).identifier() == identifier()) {
                return (i_handler.equals(((YapArray) a_dataType).i_handler));
            }
        }
        return false;
    }

    public final int getID() {
        return i_handler.getID();
    }

    public int getTypeID() {
        return i_handler.getTypeID();
    }

    public YapClass getYapClass(YapStream a_stream) {
        return i_handler.getYapClass(a_stream);
    }

    public byte identifier() {
        return YapConst.YAPARRAY;
    }
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        return null; // not supported
    }
    
    public boolean indexNullHandling() {
        return i_handler.indexNullHandling();
    }
    
    public int isSecondClass(){
        return i_handler.isSecondClass();
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        MarshallerFamily.current()._array.calculateLengths(trans, header, this, obj, topLevel);
    }

    public int objectLength(Object obj) {
        return ownLength(obj) + (_reflectArray.getLength(obj) * i_handler.linkLength());
    }
    
    public int ownLength(Object obj){
        return YapConst.OBJECT_LENGTH + YapConst.INT_LENGTH * 2;
    }
    
	public void prepareComparison(Transaction a_trans, Object obj) {
	    prepareComparison(obj);
	}

    public final Object read(MarshallerFamily mf, YapWriter a_bytes, boolean redirect) throws CorruptionException{
        return mf._array.read(this, a_bytes);
    }
    
    public Object readIndexEntry(YapReader a_reader) {
        // TODO: implement
        throw Exceptions4.virtualException();
    }
    
	public final Object readQuery(Transaction a_trans, MarshallerFamily mf, boolean withRedirection, YapReader a_reader, boolean a_toArray) throws CorruptionException{
        return mf._array.readQuery(this, a_trans, a_reader);
	}
	
	public Object read1Query(Transaction a_trans, MarshallerFamily mf, YapReader a_reader) throws CorruptionException{

        if(Deploy.debug){
            a_reader.readBegin(identifier());
        }

		int[] elements = new int[1];
        Object ret = readCreate(a_trans, a_reader, elements);
		if(ret != null){
			for (int i = 0; i < elements[0]; i++) {
                _reflectArray.set(ret, i, i_handler.readQuery(a_trans, mf, true, a_reader, true));
			}
		}
        if (Deploy.debug) {
            a_reader.readEnd();
        }
        
		return ret;
	}

    public Object read1(MarshallerFamily mf, YapWriter reader) throws CorruptionException{
        
        if (Deploy.debug) {
            reader.readBegin(identifier());
        }

		int[] elements = new int[1];
		Object ret = readCreate(reader.getTransaction(), reader, elements);
		if (ret != null){
            if(i_handler.readArray(ret, reader)){
                return ret;
            }
			for (int i = 0; i < elements[0]; i++) {
				_reflectArray.set(ret, i, i_handler.read(mf, reader, true));
			}	
		}
        
        if (Deploy.debug) {
            reader.readEnd();
        }

        return ret;
    }

	private Object readCreate(Transaction a_trans, YapReader a_reader, int[] a_elements) {
		ReflectClass[] clazz = new ReflectClass[1];
		a_elements[0] = readElementsAndClass(a_trans, a_reader, clazz);
		if (i_isPrimitive) {
			return _reflectArray.newInstance(i_handler.primitiveClassReflector(), a_elements[0]);
		} else {
			if (clazz[0] != null) {
				return _reflectArray.newInstance(clazz[0], a_elements[0]);	
			}
		}
		return null;
	}

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, YapReader[] a_bytes) {
        return this;
    }

    public void readCandidates(MarshallerFamily mf, YapReader reader, QCandidates candidates) {
        mf._array.readCandidates(this, reader, candidates);
    }
    
    public void read1Candidates(MarshallerFamily mf, YapReader reader, QCandidates candidates) {
        if(Deploy.debug){
            reader.readBegin(identifier());
        }
        
        int[] elements = new int[1];
        Object ret = readCreate(candidates.i_trans, reader, elements);
        if(ret != null){
            for (int i = 0; i < elements[0]; i++) {
                QCandidate qc = i_handler.readSubCandidate(mf, reader, candidates, true);
                if(qc != null){
                    candidates.addByIdentity(qc);
                }
            }
        }
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, YapReader reader, QCandidates candidates, boolean withIndirection) {
        reader.incrementOffset(linkLength());
        
        return null;
        
        // TODO: Here we should theoretically read through the array and collect candidates.
        // The respective construct is wild: "Contains query through an array in an array."
        // Ignore for now.
        
    }
    
    final int readElementsAndClass(Transaction a_trans, YapReader a_bytes, ReflectClass[] clazz){
        int elements = a_bytes.readInt();
        clazz[0] = i_handler.classReflector();
        if (elements < 0) {
            
            // TODO: Here is a low-frequency mistake, extremely unlikely.
            // If YapClass-ID == 99999 by accident then we will get ignore.
            
            if(elements != YapConst.IGNORE_ID){
                boolean primitive = false;
                if(!Deploy.csharp){
                    if(elements < YapConst.PRIMITIVE){
                        primitive = true;
                        elements -= YapConst.PRIMITIVE;
                    }
                }
                YapClass yc = a_trans.stream().getYapClass(- elements);
                if (yc != null) {
                    if(primitive){
                    	clazz[0] = yc.primitiveClassReflector();
                    }else{
                        clazz[0] = yc.classReflector();
                    }
                }
            }
            elements = a_bytes.readInt();
        }
        if(Debug.exceedsMaximumArrayEntries(elements, i_isPrimitive)){
            return 0;
        }
        return elements;
    }
    
    
    static Object[] toArray(YapStream a_stream, Object a_object) {
        if (a_object != null) {
        	ReflectClass claxx = a_stream.reflector().forObject(a_object);
            if (claxx.isArray()) {
                YapArray ya;
                if(a_stream.reflector().array().isNDimensional(claxx)){
                    ya = new YapArrayN(a_stream, null, false);
                } else {
                    ya = new YapArray(a_stream, null, false);
                }
                return ya.allElements(a_object);
            }
        }
        return new Object[0];
    }

    void writeClass(Object a_object, YapWriter a_bytes){
        int yapClassID = 0;
        
        Reflector reflector = a_bytes.i_trans.reflector();
        
        ReflectClass claxx = _reflectArray.getComponentType(reflector.forObject(a_object));
        
        boolean primitive = false;
        if(! Deploy.csharp){
            if(claxx.isPrimitive()){
                primitive = true;
            }
        }
        YapStream stream = a_bytes.getStream();
        if(primitive){
            claxx = stream.i_handlers.handlerForClass(stream,claxx).classReflector();
        }
        YapClass yc = stream.getYapClass(claxx, true);
        if (yc != null) {
            yapClassID = yc.getID();
        }
        if(yapClassID == 0){
            
            // TODO: This one is a terrible low-frequency blunder !!!
            // If YapClass-ID == 99999 then we will get IGNORE back.
            // Discovered on adding the primitives
            yapClassID = - YapConst.IGNORE_ID;
            
        } else{
            if(primitive){
                yapClassID -= YapConst.PRIMITIVE;
            }
        }

        a_bytes.writeInt(- yapClassID);
    }
    
    public void writeIndexEntry(YapReader a_writer, Object a_object) {
        // TODO: implement
        throw Exceptions4.virtualException();
    }
    
    public final Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, YapWriter a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        return mf._array.writeNew(this, a_object, restoreLinkOffset, a_bytes);
    }

    public void writeNew1(Object obj, YapWriter writer, int length) {
        
        if (Deploy.debug) {
            writer.writeBegin(identifier(), length);
        }
        
        writeClass(obj, writer);
		
		int elements = _reflectArray.getLength(obj);
        writer.writeInt(elements);
        
        if(! i_handler.writeArray(obj, writer)){
            for (int i = 0; i < elements; i++) {
                i_handler.writeNew(MarshallerFamily.current(), _reflectArray.get(obj, i), false, writer, true, true);
            }
        }
        
        if (Deploy.debug) {
            writer.writeEnd();
        }
        
    }

    // Comparison_______________________

    public YapComparable prepareComparison(Object obj) {
        i_handler.prepareComparison(obj);
        return this;
    }
    
    public Object current(){
        return i_handler.current();
    }
    
    public int compareTo(Object a_obj) {
        return -1;
    }
    
    public boolean isEqual(Object obj) {
        if(obj == null){
            return false;
        }
        Object[] compareWith = allElements(obj);
        for (int j = 0; j < compareWith.length; j++) {
            if (i_handler.isEqual(compareWith[j])) {
                return true;
            }
        }
        return false;
    }

    public boolean isGreater(Object obj) {
        Object[] compareWith = allElements(obj);
        for (int j = 0; j < compareWith.length; j++) {
            if (i_handler.isGreater(compareWith[j])) {
                return true;
            }
        }
        return false;
    }

    public boolean isSmaller(Object obj) {
        Object[] compareWith = allElements(obj);
        for (int j = 0; j < compareWith.length; j++) {
            if (i_handler.isSmaller(compareWith[j])) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsIndex() {
        return false;
    }

}
