/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class ArrayHandler extends VariableLengthTypeHandler implements FirstClassHandler, Comparable4 {
	
    private static final class ReflectArrayIterator extends IndexedIterator {
		private final Object _array;
		private final ReflectArray _reflectArray;

		public ReflectArrayIterator(ReflectArray reflectArray, Object array) {
			super(reflectArray.getLength(array));
			_reflectArray = reflectArray;
			_array = array;
		}

		protected Object get(int index) {
			return _reflectArray.get(_array, index);
		}
	}

	public final TypeHandler4 _handler;
    public final boolean _usePrimitiveClassReflector;

    public ArrayHandler(ObjectContainerBase container, TypeHandler4 handler, boolean usePrimitiveClassReflector) {
        super(container);
        _handler = handler;
        _usePrimitiveClassReflector = usePrimitiveClassReflector;
    }
    
    protected ArrayHandler(TypeHandler4 template) {
        this(((ArrayHandler)template).container(),((ArrayHandler)template)._handler, ((ArrayHandler)template)._usePrimitiveClassReflector );
    }

    protected ReflectArray arrayReflector(){
        return container().reflector().array();
    }

    public Iterator4 allElements(Object a_object) {
		return allElements(arrayReflector(), a_object);
    }

	public static Iterator4 allElements(final ReflectArray reflectArray, final Object array) {
		return new ReflectArrayIterator(reflectArray, array);
	}

    public final void cascadeActivation(
        Transaction trans,
        Object onObject,
        ActivationDepth depth) {
        
        if (! (_handler instanceof ClassMetadata)) {
            return;
        }
        
        Iterator4 all = allElements(onObject);
        while (all.moveNext()) {
        	final Object current = all.current();
            ActivationDepth elementDepth = descend(depth, current);
            if(elementDepth.requiresActivation()){
            	if (depth.mode().isDeactivate()) {
            		container().stillToDeactivate(trans, current, elementDepth, false);
            	} else {
            		container().stillToActivate(trans, current, elementDepth);
            	}
            }
        }
    }
    
    private ActivationDepth descend(ActivationDepth depth, Object obj){
        if(obj == null){
            return new NonDescendingActivationDepth(depth.mode());
        }
        ClassMetadata cm = classMetaDataForObject(obj);
        if(cm.isPrimitive()){
            return new NonDescendingActivationDepth(depth.mode());
        }
        return depth.descend(cm);
    }
    
    private ClassMetadata classMetaDataForObject(Object obj){
        return container().classMetadataForObject(obj);
    }
    
    public ReflectClass classReflector(){
        if(_handler instanceof BuiltinTypeHandler){
            return ((BuiltinTypeHandler)_handler).classReflector();
        }
        if(_handler instanceof ClassMetadata){
            return ((ClassMetadata)_handler).classReflector();
        }
        return container().handlers().classReflectorForHandler(_handler);
    }

    public final TreeInt collectIDs(MarshallerFamily mf, TreeInt tree, StatefulBuffer reader) throws Db4oIOException{
        return mf._array.collectIDs(this, tree, reader);
    }
    
    public final TreeInt collectIDs1(Transaction trans, TreeInt tree,
			BufferImpl reader) {
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
    
    public void delete(DeleteContext context) throws Db4oIOException {
        int address = context.readInt();
        context.readInt();  // length, not needed
        if (address <= 0) {
            return;
        }
        
        int linkOffSet = context.offset(); 
        
        if (context.cascadeDeleteDepth() > 0 && _handler instanceof ClassMetadata) {
            context.seek(address);
            if (Deploy.debug) {
            	Debug.readBegin(context, Const4.YAPARRAY);
            }
            for (int i = elementCount(context.transaction(), context); i > 0; i--) {
				_handler.delete(context);
            }
        }
        
        if(linkOffSet > 0){
        	context.seek(linkOffSet);
        }
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
    public int elementCount(Transaction trans, ReadBuffer reader) {
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
    
    public int hashCode() {
        int hc = _handler.hashCode() >> 7; 
        return _usePrimitiveClassReflector ? hc : - hc;
    }

    protected boolean handleAsByteArray(Object obj) {
        if(Deploy.csharp){
            return obj.getClass() ==  byte[].class;
        }
        return obj instanceof byte[];
    }
    
    public byte identifier() {
        return Const4.YAPARRAY;
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
	
	protected Object readCreate(Transaction trans, ReadBuffer buffer, IntByRef elements) {
		ReflectClassByRef classByRef = new ReflectClassByRef();
		elements.value = readElementsAndClass(trans, buffer, classByRef);
		ReflectClass clazz = newInstanceReflectClass(classByRef);
		if(clazz == null){
		    return null;
		}
		return arrayReflector().newInstance(clazz, elements.value);	
	}
	
    protected ReflectClass newInstanceReflectClass(ReflectClassByRef byRef){
        if(_usePrimitiveClassReflector){
            return primitiveClassReflector(); 
        }
        return byRef.value;
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, BufferImpl[] a_bytes) {
        return this;
    }

    public void readCandidates(int handlerVersion, BufferImpl reader, QCandidates candidates) throws Db4oIOException {
        reader.seek(reader.readInt());
        readSubCandidates(handlerVersion, reader, candidates);
    }
    
    public void readSubCandidates(int handlerVersion, BufferImpl reader, QCandidates candidates) {
        if(Deploy.debug){
            reader.readBegin(identifier());
        }
        IntByRef elements = new IntByRef();
        Object arr = readCreate(candidates.i_trans, reader, elements);
        if(arr == null){
            return;
        }
        readSubCandidates(handlerVersion, reader, candidates, elements.value);
    }

    protected void readSubCandidates(int handlerVersion, BufferImpl reader, QCandidates candidates, int count) {
        QueryingReadContext context = new QueryingReadContext(candidates.transaction(), handlerVersion, reader);
        for (int i = 0; i < count; i++) {
            QCandidate qc = candidates.readSubCandidate(context, _handler);
            if(qc != null){
                candidates.addByIdentity(qc);
            }
        }
    }
    
    final int readElementsAndClass(Transaction trans, ReadBuffer buffer, ReflectClassByRef clazz){
        int elements = buffer.readInt();
        if (elements < 0) {
            clazz.value = reflectClassFromElementsEntry(trans, elements);
            elements = buffer.readInt();
        } else {
    		clazz.value = classReflector();
        }
        if(Debug.exceedsMaximumArrayEntries(elements, _usePrimitiveClassReflector)){
            return 0;
        }
        return elements;
    }

   final protected int mapElementsEntry(DefragmentContext context, int orig) {
    	if(orig>=0||orig==Const4.IGNORE_ID) {
    		return orig;
    	}
    	boolean primitive=!Deploy.csharp&&orig<Const4.PRIMITIVE;
    	if(primitive) {
    		orig-=Const4.PRIMITIVE;
    	}
    	int origID=-orig;
    	int mappedID=context.mappedID(origID);
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
		return classReflector();
	}

	public static Iterator4 iterator(ReflectClass claxx, Object obj) {
		ReflectArray reflectArray = claxx.reflector().array();
        if (reflectArray.isNDimensional(claxx)) {
		    return MultidimensionalArrayHandler.allElements(reflectArray, obj);
		}
		return ArrayHandler.allElements(reflectArray, obj);
	}
    
    protected final int classID(Object obj){
        ReflectClass claxx = componentType(obj);
        boolean primitive = Deploy.csharp ? false : claxx.isPrimitive();
        if(primitive){
            claxx = container()._handlers.classMetadataForClass(container(),claxx).classReflector();
        }
        ClassMetadata classMetadata = container().produceClassMetadata(claxx);
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
	    return container().reflector();
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
        Iterator4 compareWith = allElements(obj);
        while (compareWith.moveNext()) {
            if (_handler.compareTo(compareWith.current()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isGreater(Object obj) {
        Iterator4 compareWith = allElements(obj);
        while (compareWith.moveNext()) {
            if (_handler.compareTo(compareWith.current()) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isSmaller(Object obj) {
        Iterator4 compareWith = allElements(obj);
        while (compareWith.moveNext()) {
            if (_handler.compareTo(compareWith.current()) < 0) {
                return true;
            }
        }
        return false;
    }

    public void defragment(DefragmentContext context) {
        if(Handlers4.handlesSimple(_handler)){
            context.incrementOffset(linkLength());
        }else{
        	defragIDs(context);
        }
    }
    
    private void defragIDs(DefragmentContext context) {
    	int offset= preparePayloadRead(context);
        defrag1(context);
        context.seek(offset);
    }
    
    private int preparePayloadRead(DefragmentContext context) {
        int newPayLoadOffset = context.readInt();
        context.readInt();  // skip length, not needed
        int linkOffSet = context.offset();
        context.seek(newPayLoadOffset);
        return linkOffSet;
    }

    
    public void defrag1(DefragmentContext context) {
		if (Deploy.debug) {
			Debug.readBegin(context, Const4.YAPARRAY);
		}
		int elements = readElementsDefrag(context);
		for (int i = 0; i < elements; i++) {
			_handler.defragment(context);
		}
        if (Deploy.debug) {
        	Debug.readEnd(context);
        }
    }

	protected int readElementsDefrag(DefragmentContext context) {
        int elements = context.sourceBuffer().readInt();
        context.targetBuffer().writeInt(mapElementsEntry(context, elements));
        if (elements < 0) {
            elements = context.readInt();
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
