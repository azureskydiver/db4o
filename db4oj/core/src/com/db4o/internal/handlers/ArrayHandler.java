/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.GenericReflector;

/**
 * @exclude
 */
public class ArrayHandler extends BuiltinTypeHandler {
	
    public final TypeHandler4 i_handler;
    public final boolean i_isPrimitive;
    public final ReflectArray _reflectArray;

    public ArrayHandler(ObjectContainerBase stream, TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(stream);
        i_handler = a_handler;
        i_isPrimitive = a_isPrimitive;
        _reflectArray = stream.reflector().array();
    }

    public Object[] allElements(Object a_object) {
		return allElements(_reflectArray, a_object);
    }

	public static Object[] allElements(final ReflectArray reflectArray, Object array) {
		Object[] all = new Object[reflectArray.getLength(array)];
        for (int i = all.length - 1; i >= 0; i--) {
            all[i] = reflectArray.get(array, i);
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
        if (i_handler instanceof ClassMetadata) {
            
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

    public final TreeInt collectIDs(MarshallerFamily mf, TreeInt tree, StatefulBuffer reader) throws Db4oIOException{
        return mf._array.collectIDs(this, tree, reader);
    }
    
    public final TreeInt collectIDs1(Transaction trans, TreeInt tree,
			Buffer reader) {
		if (reader == null) {
			return tree;
		}
		if (Deploy.debug) {
			reader.readBegin(identifier());
		}
		int count = elementCount(trans, reader);
		for (int i = 0; i < count; i++) {
			tree = (TreeInt) Tree.add(tree, new TreeInt(reader.readInt()));
		}
		return tree;
	}
    
    public final void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) throws Db4oIOException {
        mf._array.deleteEmbedded(this, a_bytes);
    }

    
    // FIXME: This code has not been called in any test case when the 
    //        new ArrayMarshaller was written.
    //        Apparently it only frees slots.
    //        For now the code simply returns without freeing.
    /** @param classPrimitive */
    public final void deletePrimitiveEmbedded(
        StatefulBuffer a_bytes,
        PrimitiveFieldHandler classPrimitive) {
        
		a_bytes.readInt(); //int address = a_bytes.readInt();
		a_bytes.readInt(); //int length = a_bytes.readInt();

        if(true){
            return;
        }        
        
//        if (address > 0) {
//            Transaction trans = a_bytes.getTransaction();
//            YapReader bytes =
//                a_bytes.getStream().readWriterByAddress(trans, address, length);
//            if (bytes != null) {
//                if (Deploy.debug) {
//                    bytes.readBegin(identifier());
//                }
//                for (int i = elementCount(trans, bytes); i > 0; i--) {
//                    int id = bytes.readInt();
//                    Slot slot = trans.getCurrentSlotOfID(id);
//                    
//					a_classPrimitive.free(trans, id, slot._address,slot._length);
//                }
//            }
//            
//            trans.slotFreeOnCommit(address, address, length);
//        }
    }

    /** @param trans */
    public int elementCount(Transaction trans, SlotReader reader) {
        int typeOrLength = reader.readInt();
        if (typeOrLength >= 0) {
            return typeOrLength;
        }
        return reader.readInt();
    }

    public boolean equals(Object obj) {
        if (! (obj instanceof ArrayHandler)) {
            return false;
        }
        if (((ArrayHandler) obj).identifier() != identifier()) {
            return false;
        }
        return (i_handler.equals(((ArrayHandler) obj).i_handler));
    }


    public final int getID() {
        return i_handler.getID();
    }

    public int getTypeID() {
        return i_handler.getTypeID();
    }

    public ClassMetadata getClassMetadata(ObjectContainerBase a_stream) {
        return i_handler.getClassMetadata(a_stream);
    }

    public byte identifier() {
        return Const4.YAPARRAY;
    }
    
    public TernaryBool isSecondClass(){
        return i_handler.isSecondClass();
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        MarshallerFamily.current()._array.calculateLengths(trans, header, this, obj, topLevel);
    }

    public int objectLength(Object obj) {
        return ownLength(obj) + (_reflectArray.getLength(obj) * i_handler.linkLength());
    }
    
    /** @param obj */
    public int ownLength(Object obj){
        return ownLength();
    }

	private int ownLength() {
		return Const4.OBJECT_LENGTH + Const4.INT_LENGTH * 2;
	}
    
	public void prepareComparison(Transaction a_trans, Object obj) {
	    prepareComparison(obj);
	}

	public ReflectClass primitiveClassReflector() {
		return i_handler.primitiveClassReflector();
	}
	
    public final Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
        return mf._array.read(this, a_bytes);
    }
    
	public final Object readQuery(Transaction a_trans, MarshallerFamily mf, boolean withRedirection, Buffer a_reader, boolean a_toArray) throws CorruptionException, Db4oIOException {
        return mf._array.readQuery(this, a_trans, a_reader);
	}
	
	public Object read1Query(Transaction a_trans, MarshallerFamily mf, Buffer a_reader) throws CorruptionException, Db4oIOException {

        if(Deploy.debug){
            a_reader.readBegin(identifier());
        }

        IntByRef elements = new IntByRef();
        Object ret = readCreate(a_trans, a_reader, elements);
		if(ret != null){
			for (int i = 0; i < elements.value; i++) {
                _reflectArray.set(ret, i, i_handler.readQuery(a_trans, mf, true, a_reader, true));
			}
		}
        if (Deploy.debug) {
            a_reader.readEnd();
        }
        
		return ret;
	}

    public Object read1(MarshallerFamily mf, StatefulBuffer reader) throws CorruptionException, Db4oIOException {
        
        if (Deploy.debug) {
            reader.readBegin(identifier());
        }

		IntByRef elements = new IntByRef();
		Object ret = readCreate(reader.getTransaction(), reader, elements);
		if (ret != null){
            if(i_handler.readArray(ret, reader)){
                return ret;
            }
			for (int i = 0; i < elements.value; i++) {
				_reflectArray.set(ret, i, i_handler.read(mf, reader, true));
			}	
		}
        
        if (Deploy.debug) {
            reader.readEnd();
        }

        return ret;
    }

	private Object readCreate(Transaction trans, Buffer buffer, IntByRef elements) {
		ReflectClassByRef clazz = new ReflectClassByRef();
		elements.value = readElementsAndClass(trans, buffer, clazz);
		if (i_isPrimitive) {
			return _reflectArray.newInstance(i_handler.primitiveClassReflector(), elements.value);
		} 
		if (clazz.value != null) {
			return _reflectArray.newInstance(clazz.value, elements.value);	
		}
		return null;
	}

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        return this;
    }

    public void readCandidates(MarshallerFamily mf, Buffer reader, QCandidates candidates) throws Db4oIOException {
        mf._array.readCandidates(this, reader, candidates);
    }
    
    public void read1Candidates(MarshallerFamily mf, Buffer reader, QCandidates candidates) {
        if(Deploy.debug){
            reader.readBegin(identifier());
        }
        
        IntByRef elements = new IntByRef();
        Object ret = readCreate(candidates.i_trans, reader, elements);
        if(ret != null){
            for (int i = 0; i < elements.value; i++) {
                QCandidate qc = i_handler.readSubCandidate(mf, reader, candidates, true);
                if(qc != null){
                    candidates.addByIdentity(qc);
                }
            }
        }
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        reader.incrementOffset(linkLength());
        
        return null;
        
        // TODO: Here we should theoretically read through the array and collect candidates.
        // The respective construct is wild: "Contains query through an array in an array."
        // Ignore for now.
        
    }
    
    final int readElementsAndClass(Transaction trans, Buffer buffer, ReflectClassByRef clazz){
        int elements = buffer.readInt();
        if (elements < 0) {
            clazz.value =reflectClassFromElementsEntry(trans, elements);
            elements = buffer.readInt();
        }
        else {
    		clazz.value =i_handler.classReflector();
        }
        if(Debug.exceedsMaximumArrayEntries(elements, i_isPrimitive)){
            return 0;
        }
        return elements;
    }

   final protected int mapElementsEntry(int orig,IDMapping mapping) {
    	if(orig>=0||orig==Const4.IGNORE_ID) {
    		return orig;
    	}
    	boolean primitive=!Deploy.csharp&&orig<Const4.PRIMITIVE;
    	if(primitive) {
    		orig-=Const4.PRIMITIVE;
    	}
    	int origID=-orig;
    	int mappedID=mapping.mappedID(origID);
    	int mapped=-mappedID;
    	if(primitive) {
    		mapped+=Const4.PRIMITIVE;
    	}
    	return mapped;
    }
    
	private ReflectClass reflectClassFromElementsEntry(Transaction a_trans,int elements) {

		// TODO: Here is a low-frequency mistake, extremely unlikely.
		// If YapClass-ID == 99999 by accident then we will get ignore.
		
		if(elements != Const4.IGNORE_ID){
		    boolean primitive = false;
		    if(!Deploy.csharp){
		        if(elements < Const4.PRIMITIVE){
		            primitive = true;
		            elements -= Const4.PRIMITIVE;
		        }
		    }
		    int classID = - elements;
			ClassMetadata yc = a_trans.stream().classMetadataForId(classID);
		    if (yc != null) {
		        return (primitive ? yc.primitiveClassReflector() : yc.classReflector());
		    }
		}
		return i_handler.classReflector();
	}
    
    public static Object[] toArray(ObjectContainerBase stream, Object obj) {
    	final GenericReflector reflector = stream.reflector();
		ReflectClass claxx = reflector.forObject(obj);
		ReflectArray reflectArray = reflector.array();
        if(reflectArray.isNDimensional(claxx)) {
		    return MultidimensionalArrayHandler.allElements(reflectArray, obj);
		}
		return ArrayHandler.allElements(reflectArray, obj);
    }

	void writeClass(Object a_object, StatefulBuffer a_bytes){
        int yapClassID = 0;
        
        Reflector reflector = a_bytes.getTransaction().reflector();
        
        ReflectClass claxx = _reflectArray.getComponentType(reflector.forObject(a_object));
        
        boolean primitive = false;
        if(! Deploy.csharp){
            if(claxx.isPrimitive()){
                primitive = true;
            }
        }
        ObjectContainerBase stream = a_bytes.getStream();
        if(primitive){
            claxx = stream.i_handlers.handlerForClass(stream,claxx).classReflector();
        }
        ClassMetadata yc = stream.produceClassMetadata(claxx);
        if (yc != null) {
            yapClassID = yc.getID();
        }
        if(yapClassID == 0){
            
            // TODO: This one is a terrible low-frequency blunder !!!
            // If YapClass-ID == 99999 then we will get IGNORE back.
            // Discovered on adding the primitives
            yapClassID = - Const4.IGNORE_ID;
            
        } else{
            if(primitive){
                yapClassID -= Const4.PRIMITIVE;
            }
        }

        a_bytes.writeInt(- yapClassID);
    }
    
    public final Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        return mf._array.writeNew(this, a_object, restoreLinkOffset, a_bytes);
    }

    public void writeNew1(Object obj, StatefulBuffer writer) {
        
        if (Deploy.debug) {
            writer.writeBegin(identifier());
        }
        
        writeClass(obj, writer);
		
		int elements = _reflectArray.getLength(obj);
        writer.writeInt(elements);
        
        if(obj instanceof byte[]){
            // byte[] performance optimisation
            writer.append((byte[])obj);
        }else{
            for (int i = 0; i < elements; i++) {
                i_handler.writeNew(MarshallerFamily.current(), _reflectArray.get(obj, i), false, writer, true, true);
            }
        }
        
        if (Deploy.debug) {
            writer.writeEnd();
        }
        
    }

    // Comparison_______________________

    public Comparable4 prepareComparison(Object obj) {
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

    public final void defrag(MarshallerFamily mf, ReaderPair readers, boolean redirect) {
    	if(!(i_handler.isSecondClass()==TernaryBool.YES)) {
    		mf._array.defragIDs(this, readers);
    	}
    	else {
    		readers.incrementOffset(linkLength());
    	}
    }

    public void defrag1(MarshallerFamily mf,ReaderPair readers) {
		if (Deploy.debug) {
			readers.readBegin(identifier());
		}
		int elements = readElementsDefrag(readers);
		for (int i = 0; i < elements; i++) {
			i_handler.defrag(mf,readers, true);
		}
        if (Deploy.debug) {
            readers.readEnd();
        }
    }

	protected int readElementsDefrag(ReaderPair readers) {
        int elements = readers.source().readInt();
        readers.target().writeInt(mapElementsEntry(elements,readers.mapping()));
        if (elements < 0) {
            elements = readers.readInt();
        }
		return elements;
	}

}
