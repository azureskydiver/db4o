/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers.array;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ArrayVersionHelper {
    
    public int classIDFromInfo(ObjectContainerBase container, ArrayInfo info){
        ClassMetadata classMetadata = container.produceClassMetadata(info.reflectClass());
        if (classMetadata == null) {
            if(NullableArrayHandling.disabled()){
                // TODO: This one is a terrible low-frequency blunder !!!
                // If YapClass-ID == 99999 then we will get IGNORE back.
                // Discovered on adding the primitives
                return Const4.IGNORE_ID;
            }
            return 0;
        }
        return classMetadata.getID();
        
    }
    
    public int classIdToMarshalledClassId(int classID, boolean primitive){
        if(NullableArrayHandling.disabled()){
            if(primitive){
                classID -= Const4.PRIMITIVE;
            }
            return - classID;
        }
        return classID;
    }
    
    public ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
        return isPrimitive ?   
            Handlers4.primitiveClassReflector(classMetadata, reflector) : 
            classMetadata.classReflector();
    }
    
    public boolean useJavaHandling() {
       if(NullableArrayHandling.disabled()){
           return ! Deploy.csharp;
       }
       return true;
    }
    
    public boolean hasNullBitmap(ArrayInfo info) {
        if(NullableArrayHandling.disabled()){
            return false;
        }
        return ! info.primitive();
    }
    
    public boolean isPreVersion0Format(int elementCount) {
        return false;
    }
    
    public boolean isPrimitive(Reflector reflector, ReflectClass claxx, ClassMetadata classMetadata) {
        if(NullableArrayHandling.disabled()){
            if(Deploy.csharp){
                return false;
            }
        }
        return claxx.isPrimitive();
    }
    
    public ReflectClass reflectClassFromElementsEntry(ObjectContainerBase container, ArrayInfo info, int classID) {
        
        if(NullableArrayHandling.disabled()){
            if(classID == Const4.IGNORE_ID){
                // TODO: Here is a low-frequency mistake, extremely unlikely.
                // If classID == 99999 by accident then we will get ignore.
                
                return null;
            }
                
            info.primitive(false);
            
            if(useJavaHandling()){
                if(classID < Const4.PRIMITIVE){
                    info.primitive(true);
                    classID -= Const4.PRIMITIVE;
                }
            }
            classID = - classID;
            
            ClassMetadata classMetadata0 = container.classMetadataForId(classID);
            if (classMetadata0 != null) {
                return classReflector(container.reflector(), classMetadata0, info.primitive());
            }
                
            return null;
        } 
        if(classID == 0){
            return null;
        }
        ClassMetadata classMetadata = container.classMetadataForId(classID);
        if (classMetadata == null) {
            return null;
        }
        return classReflector(container.reflector(), classMetadata, info.primitive());
    }
    
    public void writeTypeInfo(WriteContext context, ArrayInfo info) {
        if(NullableArrayHandling.disabled()){
            return;
        }
        BitMap4 typeInfoBitmap = new BitMap4(2);
        typeInfoBitmap.set(0, info.primitive());
        typeInfoBitmap.set(1, info.nullable());
        context.writeByte(typeInfoBitmap.getByte(0));
    }
    
    public void readTypeInfo(Transaction trans, ReadBuffer buffer, ArrayInfo info, int classID) {
        if(NullableArrayHandling.disabled()){
            return;
        }
        BitMap4 typeInfoBitmap = new BitMap4(buffer.readByte());
        info.primitive(typeInfoBitmap.isTrue(0));
        info.nullable(typeInfoBitmap.isTrue(1));
    }


}
