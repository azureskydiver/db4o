/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.*;
import com.db4o.cs.internal.*;
import com.db4o.internal.*;
import com.db4o.reflect.generic.*;

public class MClassMeta extends MsgObject implements MessageWithResponse {
	public boolean processAtServer() {
		ObjectContainerBase stream = stream();
		unmarshall();
		try{
			synchronized (streamLock()) {
	            ClassInfo classInfo = (ClassInfo) readObjectFromPayLoad();
	            ClassInfoHelper classInfoHelper = serverMessageDispatcher().classInfoHelper();
	            GenericClass genericClass = classInfoHelper.classMetaToGenericClass(stream().reflector(), classInfo);
	            if (genericClass != null) {
	                
    				Transaction trans = stream.systemTransaction();
    
    				ClassMetadata classMetadata = stream.produceClassMetadata(genericClass);
    				if (classMetadata != null) {
    					stream.checkStillToSet();
    					classMetadata.setStateDirty();
    					classMetadata.write(trans);
    					trans.commit();
    					StatefulBuffer returnBytes = stream
    							.readWriterByID(trans, classMetadata.getID());
    					write(Msg.OBJECT_TO_CLIENT.getWriter(returnBytes));
    					return true;
    				}
    			}
			}
		}catch(Exception e){
			if(Debug4.atHome){
				e.printStackTrace();
			}
		}
		write(Msg.FAILED);
		return true;
	}

}
