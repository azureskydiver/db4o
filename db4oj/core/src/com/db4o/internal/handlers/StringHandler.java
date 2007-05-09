/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ix.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.slots.*;
import com.db4o.reflect.*;



/**
 * @exclude
 */
public final class StringHandler extends BuiltinTypeHandler {
    
    public LatinStringIO i_stringIo; 
    
    public StringHandler(ObjectContainerBase stream, LatinStringIO stringIO) {
        super(stream);
        i_stringIo = stringIO;
    }
    
    public boolean canHold(ReflectClass claxx) {
        return claxx.equals(classReflector());
    }

    public void cascadeActivation(
        Transaction a_trans,
        Object a_object,
        int a_depth,
        boolean a_activate) {
        // default: do nothing
    }

    public ReflectClass classReflector(){
    	return _stream.i_handlers.ICLASS_STRING;
    }

    public Object comparableObject(Transaction trans, Object obj) throws ComparableConversionException {
    	return val(obj,trans.stream());
    }
    
    public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer buffer){
    	Slot slot = buffer.readSlot();
        if(slot.address() > 0  && ! mf._string.inlinedStrings()){
            buffer.getTransaction().slotFreeOnCommit(slot.address(), slot);
        }
    }
    
    public boolean isEqual(TypeHandler4 a_dataType) {
        return (this == a_dataType);
    }

    public int getID() {
        return 9;
    }

    byte getIdentifier() {
        return Const4.YAPSTRING;
    }

    public ClassMetadata getClassMetadata(ObjectContainerBase a_stream) {
        return a_stream.i_handlers.primitiveClassById(getID());
    }
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        try {
            return StringMarshaller.readShort(_stream, (Buffer)indexEntry);
        } catch (CorruptionException e) {
            
        }
        return null;
    }

    public boolean indexNullHandling() {
        return true;
    }
    
    public TernaryBool isSecondClass(){
        return TernaryBool.YES;
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection){
        MarshallerFamily.current()._string.calculateLengths(trans, header, topLevel, obj, withIndirection);
    }

    public Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
        return mf._string.readFromParentSlot(a_bytes.getStream(), a_bytes, redirect);
    }
    
    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        // virtual and do nothing
        return null;
    }

    public void readCandidates(MarshallerFamily mf, Buffer a_bytes, QCandidates a_candidates) {
        // do nothing
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
    	// FIXME catchall
        try {
            Object obj = null;
            if(withIndirection){
                obj = readQuery(candidates.i_trans, mf, withIndirection, reader, true);
            }else{
                obj = mf._string.read(_stream, reader);
            }
            if(obj != null){
                return new QCandidate(candidates, obj, 0, true);
            }
        } catch (CorruptionException e) {
        	// FIXME: should it be ignored?
        }
        return null;
    } 


    /**
     * This readIndexEntry method reads from the parent slot.
     * TODO: Consider renaming methods in Indexable4 and Typhandler4 to make direction clear.  
     * @throws CorruptionException
     */
    public Object readIndexEntry(MarshallerFamily mf, StatefulBuffer a_writer) throws CorruptionException, Db4oIOException {
		return mf._string.readIndexEntry(a_writer);
    }

    /**
     * This readIndexEntry method reads from the actual index in the file.
     * TODO: Consider renaming methods in Indexable4 and Typhandler4 to make direction clear.  
     */
    public Object readIndexEntry(Buffer reader) {
    	Slot s = new Slot(reader.readInt(), reader.readInt());
    	if (isInvalidSlot(s))
    		return null;
    	
    	return s; 
    }

	private boolean isInvalidSlot(Slot slot) {
		return (slot.address() == 0) && (slot.length() == 0);
	}
    
	public Object readQuery(Transaction a_trans, MarshallerFamily mf, boolean withRedirection, Buffer a_reader, boolean a_toArray) throws CorruptionException, Db4oIOException {
        if(! withRedirection){
            return mf._string.read(a_trans.stream(), a_reader);
        }
	    Buffer reader = mf._string.readSlotFromParentSlot(a_trans.stream(), a_reader);
	    if(a_toArray) {
	    	return mf._string.readFromOwnSlot(a_trans.stream(), reader);
	    }
	    return reader;
	}
    
    public void setStringIo(LatinStringIO a_io) {
        i_stringIo = a_io;
    }
    
    public boolean supportsIndex() {
        return true;
    }

    public void writeIndexEntry(Buffer writer, Object entry) {
        if(entry == null){
            writer.writeInt(0);
            writer.writeInt(0);
            return;
        }
         if(entry instanceof StatefulBuffer){
             StatefulBuffer entryAsWriter = (StatefulBuffer)entry;
             writer.writeInt(entryAsWriter.getAddress());
             writer.writeInt(entryAsWriter.getLength());
             return;
         }
         if(entry instanceof Slot){
             Slot s = (Slot) entry;
             writer.writeInt(s.address());
             writer.writeInt(s.length());
             return;
         }
         throw new IllegalArgumentException();
    }
    
    public Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkeOffset) {
        return mf._string.writeNew(a_object, topLevel, a_bytes, withIndirection);
    }

    public final void writeShort(String a_string, Buffer a_bytes) {
        if (a_string == null) {
            a_bytes.writeInt(0);
        } else {
            a_bytes.writeInt(a_string.length());
            i_stringIo.write(a_bytes, a_string);
        }
    }

    public int getTypeID() {
        return Const4.TYPE_SIMPLE;
    }


    // Comparison_______________________

    private Buffer i_compareTo;

    private Buffer val(Object obj) throws ComparableConversionException {
    	return val(obj,_stream);
    }

    private Buffer val(Object obj,ObjectContainerBase oc) throws ComparableConversionException {
        if(obj instanceof Buffer) {
            return (Buffer)obj;
        }
        if(obj instanceof String) {
            return StringMarshaller.writeShort(_stream, (String)obj);
        }

        if (obj instanceof Slot) {
			Slot s = (Slot) obj;
			return oc.bufferByAddress(s.address(), s.length());
		}
        
		return null;
    }

	public void prepareComparison(Transaction a_trans, Object obj) {
	    i_compareTo = (Buffer)obj;    
	}

    public Comparable4 prepareComparison(Object obj) {
        if (obj == null) {
            i_compareTo = null;
            return Null.INSTANCE;
        }
        i_compareTo = val(obj);
        return this;
    }
    
    public Object current(){
        return i_compareTo;
    }
    
    public int compareTo(Object obj) {
        if(i_compareTo == null) {
            if(obj == null) {
                return 0;
            }
            return 1;
        }
        return compare(i_compareTo, val(obj));
    }

    public boolean isEqual(Object obj) {
        if(i_compareTo == null){
            return obj == null;
        }
        return i_compareTo.containsTheSame(val(obj));
    }

    public boolean isGreater(Object obj) {
        if(i_compareTo == null){
            // this should be called for indexing only
            // object is always greater
            return obj != null;
        }
        return compare(i_compareTo, val(obj)) > 0;
    }

    public boolean isSmaller(Object obj) {
        if(i_compareTo == null){
            // this should be called for indexing only
            // object is always greater
            return false;
        }
        return compare(i_compareTo, val(obj)) < 0;
    }

    /** 
     * returns: -x for left is greater and +x for right is greater 
     *
     * TODO: You will need collators here for different languages.  
     */
    final int compare(Buffer a_compare, Buffer a_with) {
        if (a_compare == null) {
            if (a_with == null) {
                return 0;
            }
            return 1;
        }
        if (a_with == null) {
            return -1;
        }
        return compare(a_compare._buffer, a_with._buffer);
    }
    
    public static final int compare(byte[] compare, byte[] with){
        int min = compare.length < with.length ? compare.length : with.length;
        int start = Const4.INT_LENGTH;
        if(Deploy.debug) {
            start += Const4.LEADING_LENGTH;
            min -= Const4.BRACKETS_BYTES;
        }
        for(int i = start;i < min;i++) {
            if (compare[i] != with[i]) {
                return with[i] - compare[i];
            }
        }
        return with.length - compare.length;
    }

	public void defragIndexEntry(ReaderPair readers) {
		// address
		readers.copyID(false,true);
		// length
		readers.incrementIntSize();
	}

    public void defrag(MarshallerFamily mf, ReaderPair readers, boolean redirect) {
        if(! redirect){
        	readers.incrementOffset(linkLength());
        }
        else {
        	mf._string.defrag(readers);
        }
    }
}
