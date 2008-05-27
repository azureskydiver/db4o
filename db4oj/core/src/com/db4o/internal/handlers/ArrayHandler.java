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
    
    protected final ArrayVersionHelper _versionHelper;
    
    public ArrayHandler(){
        _versionHelper = createVersionHelper();
    }
    
    public ArrayHandler(TypeHandler4 handler, boolean usePrimitiveClassReflector) {
        this();
        _handler = handler;
        _usePrimitiveClassReflector = usePrimitiveClassReflector;
    }
    
    protected ArrayVersionHelper createVersionHelper() {
        return new ArrayVersionHelper();
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
        if(cm == null || cm.isPrimitive()){
            return new NonDescendingActivationDepth(depth.mode());
        }
        return depth.descend(cm);
    }
    
    private ClassMetadata classMetaDataForObject(ObjectContainerBase container, Object obj){
        return container.classMetadataForObject(obj);
    }
    
    protected ReflectClass classReflector(ObjectContainerBase container){
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
    
    protected ArrayInfo forEachElement(final AbstractBufferContext context, final Runnable elementRunnable){
        final ArrayInfo info = newArrayInfo();
        withContent(context, new Runnable() {
            public void run() {
                if (context.buffer() == null) {
                    return;
                }
                if(isUntypedByteArray(context)) {
                    return;
                }
                if (Deploy.debug) {
                    Debug.readBegin(context, identifier());
                }
                readInfo(context.transaction(), context, info);
                int elementCount = info.elementCount();
                elementCount -= reducedCountForNullBitMap(info, context);
                for (int i = 0; i < elementCount; i++) {
                    elementRunnable.run();
                }
            }
        });
        return info;
    }
    
    protected void withContent(AbstractBufferContext context, Runnable runnable){
        runnable.run();
    }
    
    private int reducedCountForNullBitMap(ArrayInfo info, final ReadBuffer context) {
        if (! hasNullBitmap(info)) {
            return 0;
        }
        return reducedCountForNullBitMap(info.elementCount(), readNullBitmap(context, info.elementCount()));
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
        forEachElement((AbstractBufferContext)context, new Runnable() {
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
	
	protected Object readCreate(Transaction trans, ReadBuffer buffer, ArrayInfo info) {
		readInfo(trans, buffer, info);
		ReflectClass clazz = newInstanceReflectClass(trans.reflector(), info);
		if(clazz == null){
		    return null;
		}
		return newInstance(arrayReflector(container(trans)), info, clazz);	
	}

    protected Object newInstance(ReflectArray arrayReflector, ArrayInfo info, ReflectClass clazz) {
        return arrayReflector.newInstance(clazz, info.elementCount());
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
    
    protected void readInfo(Transaction trans, ReadBuffer buffer, ArrayInfo info){
        int classID = buffer.readInt();
        if (! isPreVersion0Format(classID)) {
            _versionHelper.readTypeInfo(trans, buffer, info, classID);
            reflectClassFromElementsEntry(container(trans), info, classID);
            readDimensions(info, buffer);
        } else {
            info.reflectClass(classReflector(container(trans)));
            detectDimensionsPreVersion0Format(buffer, info, classID);
        }
        if(Debug.exceedsMaximumArrayEntries(info.elementCount(), _usePrimitiveClassReflector)){
            info.elementCount(0);
        }
    }

    protected void detectDimensionsPreVersion0Format(ReadBuffer buffer, ArrayInfo info, int classID) {
        info.elementCount(classID);
    }

    protected void readDimensions(ArrayInfo info, ReadBuffer buffer) {
        info.elementCount(buffer.readInt());
    }

    protected boolean isPreVersion0Format(int elementCount) {
        return _versionHelper.isPreVersion0Format(elementCount);
    }
    
	private void reflectClassFromElementsEntry(ObjectContainerBase container, ArrayInfo info, int classID) {
		info.reflectClass(_versionHelper.reflectClassFromElementsEntry(container, info, classID));
	}
	
	protected final ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
	    return _versionHelper.classReflector(reflector, classMetadata, isPrimitive);
	}

	public static Iterator4 iterator(ReflectClass claxx, Object obj) {
		ReflectArray reflectArray = claxx.reflector().array();
        if (reflectArray.isNDimensional(claxx)) {
		    return MultidimensionalArrayHandler.allElements(reflectArray, obj);
		}
		return ArrayHandler.allElements(reflectArray, obj);
	}
	
    protected boolean useJavaHandling() {
        return _versionHelper.useJavaHandling();
    }
    
    protected int classIDFromInfo(ObjectContainerBase container, ArrayInfo info){
        return _versionHelper.classIDFromInfo(container, info);
    }
    
    private final int marshalledClassID(ObjectContainerBase container, ArrayInfo info){
        return classIdToMarshalledClassId(classIDFromInfo(container, info), info.primitive());
    }
    
    public final int classIdToMarshalledClassId(int classID, boolean primitive){
        return _versionHelper.classIdToMarshalledClassId(classID, primitive);
    }

	protected final boolean isPrimitive(Reflector reflector, ReflectClass claxx, ClassMetadata classMetadata) {
	    return _versionHelper.isPrimitive(reflector, claxx, classMetadata);
    }

    private ReflectClass componentType(ObjectContainerBase container, Object obj){
	    return arrayReflector(container).getComponentType(container.reflector().forObject(obj));
	}
	
    public void defragment(DefragmentContext context) {
        if(Handlers4.handlesSimple(_handler)){
            context.incrementOffset(linkLength());
        }else{
            defragmentSlot(context);
        }
    }

    public final void defragmentSlot(DefragmentContext context) {
		if (Deploy.debug) {
			Debug.readBegin(context, Const4.YAPARRAY);
		}
        if(isUntypedByteArray(context)) {
            return;
        }
        
        int classIdOffset = context.targetBuffer().offset();
        
        ArrayInfo info = newArrayInfo();
        readInfo(context.transaction(), context, info);
        
        defragmentWriteMappedClassId(context, info, classIdOffset);
        
        int elementCount = info.elementCount();
        
        if(hasNullBitmap(info)){
            BitMap4 bitMap =  readNullBitmap(context, elementCount);
            elementCount -= reducedCountForNullBitMap(elementCount, bitMap);
        } 
        for (int i = 0; i < elementCount; i++) {
            _handler.defragment(context);
        }
        if (Deploy.debug) {
        	Debug.readEnd(context);
        }
    }

    private void defragmentWriteMappedClassId(DefragmentContext context, ArrayInfo info,
        int classIdOffset) {
        ByteArrayBuffer targetBuffer = context.targetBuffer();
        int currentOffset = targetBuffer.offset();
        targetBuffer.seek(classIdOffset);
        int classID = classIDFromInfo(container(context), info);
        int mappedID = context.mappedID(classID);
        final int marshalledMappedId = classIdToMarshalledClassId(mappedID, info.primitive());
        targetBuffer.writeInt(marshalledMappedId);
        targetBuffer.seek(currentOffset);
    }

    private boolean isUntypedByteArray(BufferContext context) {
        return _handler instanceof UntypedFieldHandler  && handleAsByteArray(context);
    }
    
    private boolean handleAsByteArray(BufferContext context){
        int offset = context.offset();
        ArrayInfo info = newArrayInfo();
        readInfo(context.transaction(), context, info);
        boolean isByteArray = context.transaction().reflector().forClass(byte.class).equals(info.reflectClass());
        context.seek(offset);
        return isByteArray;
    }

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, identifier());
        }
        ArrayInfo info = newArrayInfo();
        Object array = readCreate(context.transaction(), context, info);
		readElements(context, info, array);
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        return array;
    }

    protected void readElements(ReadContext context, ArrayInfo info, Object array) {
        readInto(context, info, array);
    }
    
    protected ArrayInfo newArrayInfo() {
        return new ArrayInfo();
    }

    protected final void readInto (ReadContext context, ArrayInfo info, Object array) {
        if (array == null){
            return;
        }
        if(handleAsByteArray(array)){
            context.readBytes((byte[])array); // byte[] performance optimisation
            return;
        }
        if (hasNullBitmap(info)) {
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

	protected BitMap4 readNullBitmap(ReadBuffer context, int length) {
	    return context.readBitMap(length);
	}
    
    protected final boolean hasNullBitmap(ArrayInfo info) {
        return _versionHelper.hasNullBitmap(info);
	}

	public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, identifier());
        }
        ArrayInfo info = newArrayInfo();
        analyze(container(context), obj, info);
        writeInfo(context, info);
        
        writeElements(context, obj, info);
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }

    protected void writeElements(WriteContext context, Object obj, ArrayInfo info) {
        if(handleAsByteArray(obj)){
            context.writeBytes((byte[])obj);  // byte[] performance optimisation
        }else{        	
            if (hasNullBitmap(info)) {
                BitMap4 nullItems = nullItemsMap(arrayReflector(container(context)), obj);
                writeNullBitmap(context, nullItems);
                for (int i = 0; i < info.elementCount(); i++) {
                    if (!nullItems.isTrue(i)) {
                        context.writeObject(_handler, arrayReflector(container(context)).get(obj, i));
                    }
                }
            } else {
                for (int i = 0; i < info.elementCount(); i++) {
                    context.writeObject(_handler, arrayReflector(container(context)).get(obj, i));
                }
            }
        }
    }

	protected void writeInfo(WriteContext context, ArrayInfo info) {
	    writeHeader(context, info);
	    writeDimensions(context, info);
    }

    private void writeHeader(WriteContext context, ArrayInfo info) {
        context.writeInt(marshalledClassID(container(context), info));
        _versionHelper.writeTypeInfo(context, info);
    }

    protected void writeDimensions(WriteContext context, ArrayInfo info) {
        context.writeInt(info.elementCount());
    }

    protected void analyze(ObjectContainerBase container, Object obj, ArrayInfo info) {
	    
        ReflectClass claxx = componentType(container, obj);
        
        ClassMetadata classMetadata = container.produceClassMetadata(claxx);
        boolean primitive = isPrimitive(container.reflector(), claxx, classMetadata); 

        if(primitive){
            claxx = classMetadata.classReflector();
        }
        
        info.primitive(primitive);
        info.reflectClass(claxx);
        analyzeDimensions(container, obj, info);
    }
    
    protected void analyzeDimensions(ObjectContainerBase container, Object obj, ArrayInfo info){
        info.elementCount(arrayReflector(container).getLength(obj));
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
