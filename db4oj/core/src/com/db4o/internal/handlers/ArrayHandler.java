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
public class ArrayHandler implements FirstClassHandler, Comparable4, TypeHandler4, VariableLengthTypeHandler, EmbeddedTypeHandler, CompositeTypeHandler, CollectIdHandler{
    
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
    
    public void collectIDs(final CollectIdContext context) {
        forEachElement(context, new Runnable() {
            public void run() {
                context.addId();
            }
        });
    }
    
    protected void forEachElement(final BufferContext context, final Runnable elementRunnable){
        withContent(context, new Runnable() {
            public void run() {
                if (context.buffer() == null) {
                    return;
                }
                if (Deploy.debug) {
                    Debug.readBegin(context, identifier());
                }
                int elementCount = elementCount(context.transaction(), context);
                elementCount -= reducedCountForNullBitMap(context, elementCount);
                for (int i = 0; i < elementCount; i++) {
                    elementRunnable.run();
                }
            }
        });
    }
    
    protected void withContent(BufferContext context, Runnable runnable){
        runnable.run();
    }
    
    private int reducedCountForNullBitMap(final ReadBuffer context, int count) {
        if (! hasNullBitmap()) {
            return 0;
        }
        return reducedCountForNullBitMap(count, readNullBitmap(context, count));
    }

    private int reducedCountForNullBitMap(int count, BitMap4 bitMap) {
        int nullCount = 0;
        for (int i = 0; i < count; i++) {
            if(bitMap.isTrue(i)){
                nullCount++;
            }
        }
        return nullCount;
    }
    
    public void delete(final DeleteContext context) throws Db4oIOException {
        if (! cascadeDelete(context)) {
            return;
        }
        forEachElement((BufferContext)context, new Runnable() {
            public void run() {
                _handler.delete(context);
            }
        });
    }

    private boolean cascadeDelete(DeleteContext context) {
        return context.cascadeDelete() && _handler instanceof ClassMetadata;
    }

    
    // FIXME: This code has not been called in any test case when the 
    //        new ArrayMarshaller was written.
    //        Apparently it only frees slots.
    //        For now the code simply returns without freeing.
    /** @param classPrimitive */
    public final void deletePrimitiveEmbedded(
        StatefulBuffer buffer,
        PrimitiveFieldHandler classPrimitive) {
        
		buffer.readInt(); //int address = a_bytes.readInt();
		buffer.readInt(); //int length = a_bytes.readInt();

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
	
	protected final Object readCreate(Transaction trans, ReadBuffer buffer, ArrayInfo info) {
		readElementsAndClass(trans, buffer, info);
		ReflectClass clazz = newInstanceReflectClass(trans.reflector(), info);
		if(clazz == null){
		    return null;
		}
		return arrayReflector(container(trans)).newInstance(clazz, info.elementCount());	
	}
	
	protected final ReflectClass newInstanceReflectClass(Reflector reflector, ArrayInfo info){
        if(_usePrimitiveClassReflector){
            return primitiveClassReflector(reflector); 
        }
        return info.reflectClass();
	}
	
    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, ByteArrayBuffer[] a_bytes) {
        return this;
    }

    public void readCandidates(final QueryingReadContext context) {
        final QCandidates candidates = context.candidates();
        forEachElement(context, new Runnable() {
            public void run() {
                QCandidate qc = candidates.readSubCandidate(context, _handler);
                if(qc != null){
                    candidates.addByIdentity(qc);
                }
            }
        });
    }
    
    protected void readSubCandidates(final QueryingReadContext context, int count) {
        QCandidates candidates = context.candidates();
        for (int i = 0; i < count; i++) {
            QCandidate qc = candidates.readSubCandidate(context, _handler);
            if(qc != null){
                candidates.addByIdentity(qc);
            }
        }
    }
    
    protected final int readElementsAndClass(Transaction trans, ReadBuffer buffer, ArrayInfo info){
        info.elementCount(buffer.readInt());
        if (newerArrayFormat(info.elementCount())) {
            reflectClassFromElementsEntry(trans, info);
            info.elementCount(buffer.readInt());
        } else {
            info.reflectClass(classReflector(container(trans)));
        }
        if(Debug.exceedsMaximumArrayEntries(info.elementCount(), _usePrimitiveClassReflector)){
            return 0;
        }
        return info.elementCount();
    }

    private boolean newerArrayFormat(int elements) {
        return elements < 0;
    }

   final protected int mapElementsEntry(DefragmentContext context, int orig) {
    	if( orig>=0 || orig==Const4.IGNORE_ID ) {
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
    
	private void reflectClassFromElementsEntry(Transaction trans, ArrayInfo info) {

		// TODO: Here is a low-frequency mistake, extremely unlikely.
		// If YapClass-ID == 99999 by accident then we will get ignore.
		
		if(info.elementCount() != Const4.IGNORE_ID){
		    info.primitive(false);
		    
		    if(useJavaHandling()){
		        if(info.elementCount() < Const4.PRIMITIVE){
		            info.primitive(true);
		            info.elementCount(info.elementCount() - Const4.PRIMITIVE) ;
		        }
		    }
		    int classID = - info.elementCount();
			ClassMetadata classMetadata = container(trans).classMetadataForId(classID);
		    if (classMetadata != null) {
		        info.reflectClass( classReflector(trans.reflector(), classMetadata, info.primitive()));
		        return;
		    }
		}
		info.reflectClass(classReflector(container(trans)));
	}
	
	protected ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
	    return (isPrimitive?   Handlers4.primitiveClassReflector(classMetadata, reflector) : classMetadata.classReflector());
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
    
    protected final int classID(ObjectContainerBase container, Object obj){
        ReflectClass claxx = componentType(container, obj);
        
        boolean primitive = isPrimitive(claxx); 
        
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
        
        // FIXME: Shouldn't we be beyound the array slot now?
        context.seek(offset);
    }
    
    protected int preparePayloadRead(DefragmentContext context) {
    	return context.offset();
    }

    public final void defrag1(DefragmentContext context) {
		if (Deploy.debug) {
			Debug.readBegin(context, Const4.YAPARRAY);
		}
		defrag2(context);
        if (Deploy.debug) {
        	Debug.readEnd(context);
        }
    }

    public void defrag2(DefragmentContext context) {
		if(isUntypedByteArray(context)) {
		    return;
		}
		int elementCount = readElementCountDefrag(context);
		if(hasNullBitmap()){
            BitMap4 bitMap =  defragmentNullBitmap(context, elementCount);
            elementCount -= reducedCountForNullBitMap(elementCount, bitMap);
		} 
        for (int i = 0; i < elementCount; i++) {
            _handler.defragment(context);
        }
    }

    private boolean isUntypedByteArray(DefragmentContext context) {
        return _handler instanceof UntypedFieldHandler  && handleAsByteArray(context);
    }
    
    private boolean handleAsByteArray(DefragmentContext context){
        int offset = context.offset();
        ArrayInfo info = new ArrayInfo();
        readElementsAndClass(context.transaction(), context, info);
        boolean isByteArray = context.transaction().reflector().forClass(byte.class).equals(info.reflectClass());
        context.seek(offset);
        return isByteArray;
    }

	private BitMap4 defragmentNullBitmap(DefragmentContext context, int elements) {
        if (! hasNullBitmap()) {
            return null;
        }
        BitMap4 nullBitmap = readNullBitmap(context.sourceBuffer(), elements);
        writeNullBitmap(context.targetBuffer(), nullBitmap);
        return nullBitmap;
    }	

	protected int readElementCountDefrag(DefragmentContext context) {
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
        ArrayInfo info = new ArrayInfo();
        Object array = readCreate(context.transaction(), context, info);
        if (array != null){
            if(handleAsByteArray(array)){
                context.readBytes((byte[])array); // byte[] performance optimisation
            } else{
				if (hasNullBitmap()) {
                    BitMap4 nullBitMap = readNullBitmap(context, info.elementCount());                    
                    for (int i = 0; i < info.elementCount(); i++) {
                        Object obj = nullBitMap.isTrue(i) ? null :context.readObject(_handler);
                        arrayReflector(container(context)).set(array, i, obj);
                    }
            	} else {
                    for (int i = 0; i < info.elementCount(); i++) {
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

	protected BitMap4 readNullBitmap(ReadBuffer context, int length) {
	    return context.readBitMap(length);
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

	private void writeNullBitmap(WriteBuffer context, BitMap4 bitMap) {
		context.writeBytes(bitMap.bytes());
	}

    protected BitMap4 nullItemsMap(ReflectArray reflector, Object array) {
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
