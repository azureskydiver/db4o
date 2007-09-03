/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.GenericReflector;

/**
 * @exclude
 */
public class ArrayHandler extends BuiltinTypeHandler implements FirstClassHandler {
	
    public final TypeHandler4 _handler;
    public final boolean _isPrimitive;

    public ArrayHandler(ObjectContainerBase stream, TypeHandler4 a_handler, boolean a_isPrimitive) {
        super(stream);
        _handler = a_handler;
        _isPrimitive = a_isPrimitive;
    }
    
    protected ReflectArray arrayReflector(){
        return _stream.reflector().array();
    }

    public Object[] allElements(Object a_object) {
		return allElements(arrayReflector(), a_object);
    }

	public static Object[] allElements(final ReflectArray reflectArray, Object array) {
		Object[] all = new Object[reflectArray.getLength(array)];
        for (int i = all.length - 1; i >= 0; i--) {
            all[i] = reflectArray.get(array, i);
        }
        return all;
	}

    public final void cascadeActivation(
        Transaction a_trans,
        Object a_object,
        int a_depth,
        boolean a_activate) {
        // We simply activate all Objects here
        if (_handler instanceof ClassMetadata) {
            
            a_depth --;
            
            Object[] all = allElements(a_object);
            if (a_activate) {
                for (int i = all.length - 1; i >= 0; i--) {
                    _stream.stillToActivate(a_trans, all[i], a_depth);
                }
            } else {
                for (int i = all.length - 1; i >= 0; i--) {
                  _stream.stillToDeactivate(a_trans, all[i], a_depth, false);
                }
            }
        }
    }
    
    public ReflectClass classReflector(){
    	return _handler.classReflector();
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
        
    }

    /** @param trans */
    public int elementCount(Transaction trans, SlotBuffer reader) {
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
        return (_handler.equals(((ArrayHandler) obj)._handler));
    }


    public final int getID() {
        return _handler.getID();
    }

    private boolean handleAsByteArray(Object obj) {
        if(Deploy.csharp){
            return obj.getClass() ==  byte[].class;
        }
        return obj instanceof byte[];
    }
    
    public byte identifier() {
        return Const4.YAPARRAY;
    }
    
    public int objectLength(Object obj) {
        return ownLength(obj) + (arrayReflector().getLength(obj) * _handler.linkLength());
    }
    
    /** @param obj */
    public int ownLength(Object obj){
        return ownLength();
    }

	private int ownLength() {
		return Const4.OBJECT_LENGTH + Const4.INT_LENGTH * 2;
	}
    
	public ReflectClass primitiveClassReflector() {
		return Handlers4.primitiveClassReflector(_handler);
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
			    arrayReflector().set(ret, i, _handler.readQuery(a_trans, mf, true, a_reader, true));
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
		Object array = readCreate(reader.getTransaction(), reader, elements);
		
		if (array != null){
	        if(handleAsByteArray(array)){
	            reader.readBytes((byte[])array);
	        } else{
    			for (int i = 0; i < elements.value; i++) {
    				arrayReflector().set(array, i, _handler.read(mf, reader, true));
    			}
	        }
		}
        
        if (Deploy.debug) {
            reader.readEnd();
        }

        return array;
    }

	private Object readCreate(Transaction trans, ReadBuffer buffer, IntByRef elements) {
		ReflectClassByRef clazz = new ReflectClassByRef();
		elements.value = readElementsAndClass(trans, buffer, clazz);
		if (_isPrimitive) {
			return arrayReflector().newInstance(primitiveClassReflector(), elements.value);
		} 
		if (clazz.value != null) {
			return arrayReflector().newInstance(clazz.value, elements.value);	
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
                QCandidate qc = _handler.readSubCandidate(mf, reader, candidates, true);
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
    
    final int readElementsAndClass(Transaction trans, ReadBuffer buffer, ReflectClassByRef clazz){
        int elements = buffer.readInt();
        if (elements < 0) {
            clazz.value = reflectClassFromElementsEntry(trans, elements);
            elements = buffer.readInt();
        }
        else {
    		clazz.value =_handler.classReflector();
        }
        if(Debug.exceedsMaximumArrayEntries(elements, _isPrimitive)){
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
    
	private ReflectClass reflectClassFromElementsEntry(Transaction trans,int elements) {

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
			ClassMetadata classMetadata = trans.container().classMetadataForId(classID);
		    if (classMetadata != null) {
		        return (primitive ?   Handlers4.primitiveClassReflector(classMetadata) : classMetadata.classReflector());
		    }
		}
		return _handler.classReflector();
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
    
    protected final int classID(Object obj){
        ReflectClass claxx = componentType(obj);
        boolean primitive = Deploy.csharp ? false : claxx.isPrimitive();
        if(primitive){
            claxx = _stream._handlers.handlerForClass(_stream,claxx).classReflector();
        }
        ClassMetadata classMetadata = _stream.produceClassMetadata(claxx);
        if (classMetadata == null) {
            // TODO: This one is a terrible low-frequency blunder !!!
            // If YapClass-ID == 99999 then we will get IGNORE back.
            // Discovered on adding the primitives
            return Const4.IGNORE_ID;
        }
        int classID = classMetadata.getID();
        if(primitive){
            classID -= Const4.PRIMITIVE;
        }
        return -classID;
    }

	private ReflectClass componentType(Object obj){
	    return arrayReflector().getComponentType(reflector().forObject(obj));
	}
	
	private Reflector reflector(){
	    return _stream.reflector();
	}
	

    // Comparison_______________________

    public Comparable4 prepareComparison(Object obj) {
        _handler.prepareComparison(obj);
        return this;
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
            if (_handler.compareTo(compareWith[j]) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isGreater(Object obj) {
        Object[] compareWith = allElements(obj);
        for (int j = 0; j < compareWith.length; j++) {
            if (_handler.compareTo(compareWith[j]) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isSmaller(Object obj) {
        Object[] compareWith = allElements(obj);
        for (int j = 0; j < compareWith.length; j++) {
            if (_handler.compareTo(compareWith[j]) < 0) {
                return true;
            }
        }
        return false;
    }

    public final void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
        if(Handlers4.handlesSimple(_handler)){
            readers.incrementOffset(linkLength());
        }else{
            mf._array.defragIDs(this, readers);
        }
    }
    
    public void defrag1(MarshallerFamily mf,BufferPair readers) {
		if (Deploy.debug) {
			readers.readBegin(identifier());
		}
		int elements = readElementsDefrag(readers);
		for (int i = 0; i < elements; i++) {
			_handler.defrag(mf,readers, true);
		}
        if (Deploy.debug) {
            readers.readEnd();
        }
    }

	protected int readElementsDefrag(BufferPair readers) {
        int elements = readers.source().readInt();
        readers.target().writeInt(mapElementsEntry(elements,readers.mapping()));
        if (elements < 0) {
            elements = readers.readInt();
        }
		return elements;
	}
	
    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPARRAY);
        }
        IntByRef elements = new IntByRef();
        Object array = readCreate(context.transaction(), context, elements);
        if (array != null){
            if(handleAsByteArray(array)){
                context.readBytes((byte[])array); // byte[] performance optimisation
            } else{
                for (int i = 0; i < elements.value; i++) {
                    arrayReflector().set(array, i, context.readObject(_handler));
                }
            }
        }
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        return array;
    }
    
    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPARRAY);
        }
        int classID = classID(obj);
        context.writeInt(classID);
        int elementCount = arrayReflector().getLength(obj);
        context.writeInt(elementCount);
        if(handleAsByteArray(obj)){
            context.writeBytes((byte[])obj);  // byte[] performance optimisation
        }else{
            for (int i = 0; i < elementCount; i++) {
                context.writeObject(_handler, arrayReflector().get(obj, i));
            }
        }
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    
}
