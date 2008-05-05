/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * This is the latest version, the one that should be used.
 * @exclude
 */
public class ArrayHandler implements FirstClassHandler, Comparable4, TypeHandler4, VariableLengthTypeHandler, EmbeddedTypeHandler, CompositeTypeHandler{
    
	private TypeHandler4 _handler;
	
    private boolean _usePrimitiveClassReflector;
    
    public ArrayHandler(TypeHandler4 handler, boolean usePrimitiveClassReflector) {
        _handler = handler;
        _usePrimitiveClassReflector = usePrimitiveClassReflector;
    }
    
    public ArrayHandler(){
        // required for reflection cloning
    }
    
    protected ArrayHandler(ArrayHandler template, HandlerRegistry registry, int version) {
        this(registry.correctHandlerVersion(template._handler, version), template._usePrimitiveClassReflector);
    }

    protected ReflectArray arrayReflector(ObjectContainerBase container){
        return container.reflector().array();
    }

    public Iterator4 allElements(ObjectContainerBase container, Object a_object) {
		return allElements(arrayReflector(container), a_object);
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
        
        ObjectContainerBase container = container(trans);
        Iterator4 all = allElements(container, onObject);
        while (all.moveNext()) {
        	final Object current = all.current();
            ActivationDepth elementDepth = descend(container, depth, current);
            if(elementDepth.requiresActivation()){
            	if (depth.mode().isDeactivate()) {
            	    container.stillToDeactivate(trans, current, elementDepth, false);
            	} else {
            	    container.stillToActivate(trans, current, elementDepth);
            	}
            }
        }
    }

    ObjectContainerBase container(Transaction trans) {
        return trans.container();
    }
    
    private ActivationDepth descend(ObjectContainerBase container, ActivationDepth depth, Object obj){
        if(obj == null){
            return new NonDescendingActivationDepth(depth.mode());
        }
        ClassMetadata cm = classMetaDataForObject(container, obj);
        if(cm.isPrimitive()){
            return new NonDescendingActivationDepth(depth.mode());
        }
        return depth.descend(cm);
    }
    
    private ClassMetadata classMetaDataForObject(ObjectContainerBase container, Object obj){
        return container.classMetadataForObject(obj);
    }
    
    private ReflectClass classReflector(ObjectContainerBase container){
        if(_handler instanceof BuiltinTypeHandler){
            return ((BuiltinTypeHandler)_handler).classReflector();
        }
        if(_handler instanceof ClassMetadata){
            return ((ClassMetadata)_handler).classReflector();
        }
        return container.handlers().classReflectorForHandler(_handler);
    }

    public final TreeInt collectIDs(MarshallerFamily mf, TreeInt tree, StatefulBuffer reader) throws Db4oIOException{
        return mf._array.collectIDs(this, tree, reader);
    }
    
    public final TreeInt collectIDs1(Transaction trans, TreeInt tree,
			ByteArrayBuffer reader) {
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
        
        if (context.cascadeDelete() && _handler instanceof ClassMetadata) {
            
            if (Deploy.debug) {
            	Debug.readBegin(context, Const4.YAPARRAY);
            }
            
            int elementCount = elementCount(context.transaction(), context);
            
            if (hasNullBitmap()) {            	
            	int nullBitmapLength = context.readInt();
				context.seek(context.offset() + nullBitmapLength);
            }
            
			for (int i = elementCount; i > 0; i--) {
				_handler.delete(context);
            }
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
        ArrayHandler other = (ArrayHandler) obj;
        if (other.identifier() != identifier()) {
            return false;
        }
        if(_handler == null){
            return other._handler == null;
        }
        return _handler.equals(other._handler)  && _usePrimitiveClassReflector == other._usePrimitiveClassReflector;
    }
    
    public int hashCode() {
        if(_handler == null){
            return HASHCODE_FOR_NULL; 
        }
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
    
	public ReflectClass primitiveClassReflector(Reflector reflector) {
		return Handlers4.primitiveClassReflector(_handler, reflector);
	}
	
	protected Object readCreate(Transaction trans, ReadBuffer buffer, IntByRef elements) {
		ReflectClassByRef classByRef = new ReflectClassByRef();
		elements.value = readElementsAndClass(trans, buffer, classByRef);
		ReflectClass clazz = newInstanceReflectClass(trans.reflector(), classByRef);
		if(clazz == null){
		    return null;
		}
		return arrayReflector(container(trans)).newInstance(clazz, elements.value);	
	}
	
    protected ReflectClass newInstanceReflectClass(Reflector reflector, ReflectClassByRef byRef){
        if(_usePrimitiveClassReflector){
            return primitiveClassReflector(reflector); 
        }
        return byRef.value;
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, ByteArrayBuffer[] a_bytes) {
        return this;
    }

    public void readCandidates(int handlerVersion, ByteArrayBuffer reader, QCandidates candidates) throws Db4oIOException {
        readSubCandidates(handlerVersion, reader, candidates);
    }
    
    public void readSubCandidates(int handlerVersion, ByteArrayBuffer reader, QCandidates candidates) {
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

    protected void readSubCandidates(int handlerVersion, ByteArrayBuffer reader, QCandidates candidates, int count) {
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
        if (newerArrayFormat(elements)) {
            clazz.value = reflectClassFromElementsEntry(trans, elements);
            elements = buffer.readInt();
        } else {
    		clazz.value = classReflector(container(trans));
        }
        if(Debug.exceedsMaximumArrayEntries(elements, _usePrimitiveClassReflector)){
            return 0;
        }
        return elements;
    }

    private boolean newerArrayFormat(int elements) {
        return elements < 0;
    }

   final protected int mapElementsEntry(DefragmentContext context, int orig) {
    	if(orig>=0||orig==Const4.IGNORE_ID) {
    		return orig;
    	}
    	
    	// TODO: We changed the following line in the NullableArrayHandling 
    	//       refactoring. Behaviour may have to be different for older
    	//       ArrayHandler versions.
    	boolean primitive = useJavaHandling() && (orig < Const4.PRIMITIVE);
    	
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
		    
		    if(useJavaHandling()){
		        if(elements < Const4.PRIMITIVE){
		            primitive = true;
		            elements -= Const4.PRIMITIVE;
		        }
		    }
		    int classID = - elements;
			ClassMetadata classMetadata = container(trans).classMetadataForId(classID);
		    if (classMetadata != null) {
		    	if(readingDotNetBeforeVersion4()){
		    		primitive = classMetadata.isValueType();
		    	}
		        return (primitive ?   Handlers4.primitiveClassReflector(classMetadata, trans.reflector()) : classMetadata.classReflector());
		    }
		}
		return classReflector(container(trans));
	}

	public static Iterator4 iterator(ReflectClass claxx, Object obj) {
		ReflectArray reflectArray = claxx.reflector().array();
        if (reflectArray.isNDimensional(claxx)) {
		    return MultidimensionalArrayHandler.allElements(reflectArray, obj);
		}
		return ArrayHandler.allElements(reflectArray, obj);
	}
	
    protected boolean useJavaHandling() {
       if(NullableArrayHandling.enabled()){
           return true;
       }
       return ! Deploy.csharp;
    }
    
	protected boolean readingDotNetBeforeVersion4() {
	    return false;
	}
    
    protected final int classID(ObjectContainerBase container, Object obj){
        ReflectClass claxx = componentType(container, obj);
        
        boolean primitive = isPrimitive(claxx); 
            // useOldNetHandling() ? false : claxx.isPrimitive();
        
        if(primitive){
            claxx = container.produceClassMetadata(claxx).classReflector();
        }
        ClassMetadata classMetadata = container.produceClassMetadata(claxx);
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

	protected boolean isPrimitive(ReflectClass claxx) {
	    if(NullableArrayHandling.enabled()){
	        return claxx.isPrimitive();
	    }
        if(Deploy.csharp){
            return false;
        }
        return claxx.isPrimitive();
    }

    private ReflectClass componentType(ObjectContainerBase container, Object obj){
	    return arrayReflector(container).getComponentType(container.reflector().forObject(obj));
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
    
    protected int preparePayloadRead(DefragmentContext context) {
    	return context.offset();
    }

    public void defrag1(DefragmentContext context) {
		if (Deploy.debug) {
			Debug.readBegin(context, Const4.YAPARRAY);
		}
		defrag2(context);
        if (Deploy.debug) {
        	Debug.readEnd(context);
        }
    }

    public void defrag2(DefragmentContext context) {
		if(!(_handler instanceof UntypedFieldHandler)) {
			defragElements(context);
			return;
		}
		ReflectClassByRef clazzRef = new ReflectClassByRef();
		int offset = context.offset();
		int numElements = readElementsAndClass(context.transaction(), context, clazzRef);
		if(!(context.transaction().reflector().forClass(byte.class).equals(clazzRef.value))) {
			// FIXME behavior should be identical to seek/defrag below - why failure?
			// defragElements(context, numElements);
			context.seek(offset);
			defragElements(context);
			return;
		}
		context.incrementOffset(numElements);
    }

	private void defragElements(DefragmentContext context) {
		int elements = readElementsDefrag(context);
		defragElements(context, elements);
	}

	private void defragElements(DefragmentContext context, int elements) {
		defragmentNullBitmap(context, elements);
		
		for (int i = 0; i < elements; i++) {
			_handler.defragment(context);
		}
	}
	
    private void defragmentNullBitmap(DefragmentContext context, int elements) {
        if (hasNullBitmap()) {
            BitMap4 nullBitmap = readNullBitmap(context.sourceBuffer(), elements);
            writeNullBitmap(context.targetBuffer(), nullBitmap);
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
				if (hasNullBitmap()) {
                    BitMap4 nullBitMap = readNullBitmap(context, elements.value);                    
                    for (int i = 0; i < elements.value; i++) {
                        Object obj = nullBitMap.isTrue(i) ? null :context.readObject(_handler);
                        arrayReflector(container(context)).set(array, i, obj);
                    }
            	} else {
                    for (int i = 0; i < elements.value; i++) {
                        arrayReflector(container(context)).set(array, i, context.readObject(_handler));
                    }
            	}
            }
        }
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        return array;
    }

	private BitMap4 readNullBitmap(ReadBuffer context, int length) {
		byte[] bitMapBytes = new byte[context.readInt()];
		context.readBytes(bitMapBytes);
		return new BitMap4(bitMapBytes, 0, length);
	}
    
    protected boolean hasNullBitmap() {
        return NullableArrayHandling.enabled();
	}

	public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPARRAY);
        }
        int classID = classID(container(context), obj);
        context.writeInt(classID);
        int elementCount = arrayReflector(container(context)).getLength(obj);
        context.writeInt(elementCount);
        if(handleAsByteArray(obj)){
            context.writeBytes((byte[])obj);  // byte[] performance optimisation
        }else{        	
            if (hasNullBitmap()) {
                BitMap4 nullItems = nullItemsMap(arrayReflector(container(context)), obj);
                writeNullBitmap(context, nullItems);
                for (int i = 0; i < elementCount; i++) {
                    if (!nullItems.isTrue(i)) {
                        context.writeObject(_handler, arrayReflector(container(context)).get(obj, i));
                    }
                }
            } else {
                for (int i = 0; i < elementCount; i++) {
                    context.writeObject(_handler, arrayReflector(container(context)).get(obj, i));
                }
            }
        }
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }

	private void writeNullBitmap(WriteBuffer context, BitMap4 nullItems) {
		byte[] nullMapBytes = nullItems.bytes();
		
		context.writeInt(nullMapBytes.length);
		context.writeBytes(nullMapBytes);
	}

    private BitMap4 nullItemsMap(ReflectArray reflector, Object array) {
		int arrayLength = reflector.getLength(array);
    	BitMap4 nullBitMap = new BitMap4(arrayLength);
    	for (int i = 0; i < arrayLength; i++) {
			if (reflector.get(array, i) == null) {
				nullBitMap.set(i, true);
			}
		}
    	return nullBitMap;
	}

	ObjectContainerBase container(Context context) {
        return context.transaction().container();
    }

	public PreparedComparison prepareComparison(Context context, Object obj) {
		return new PreparedArrayContainsComparison(context, this, _handler, obj);
	}
	
    public int linkLength() {
        return Const4.INDIRECTION_LENGTH;
    }

    public TypeHandler4 genericTemplate() {
        return new ArrayHandler();
    }
    
    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        ArrayHandler original = (ArrayHandler) typeHandlerCloneContext.original;
        ArrayHandler cloned = (ArrayHandler) Reflection4.newInstance(this);
        cloned._usePrimitiveClassReflector = original._usePrimitiveClassReflector;
        cloned._handler = typeHandlerCloneContext.correctHandlerVersion(original.delegateTypeHandler());  
        return cloned;
    }

    public TypeHandler4 delegateTypeHandler() {
        return _handler;
    }
    
    private static final int HASHCODE_FOR_NULL = 9141078; 
    
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

    
}
