/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.reflect.generic.*;

public class MClassMeta extends MsgObject implements ServerSideMessage {
	public boolean processAtServer() {
		ObjectContainerBase stream = stream();
		unmarshall();
		try{
			synchronized (streamLock()) {
	            ClassInfo classMeta = (ClassInfo) readObjectFromPayLoad();
	            ClassInfoHelper classInfoHelper = serverMessageDispatcher().classInfoHelper();
	            GenericClass genericClass = classInfoHelper.classMetaToGenericClass(stream().reflector(), classMeta);
	            if (genericClass != null) {
	                
    				Transaction trans = stream.systemTransaction();
    
    				ClassMetadata yapClass = stream.produceClassMetadata(genericClass);
    				if (yapClass != null) {
    					stream.checkStillToSet();
    					yapClass.setStateDirty();
    					yapClass.write(trans);
    					trans.commit();
    					StatefulBuffer returnBytes = stream
    							.readWriterByID(trans, yapClass.getID());
    					write(Msg.OBJECT_TO_CLIENT.getWriter(returnBytes));
    					return true;
    				}
    			}
			}
		}catch(Exception e){
			if(Debug.atHome){
				e.printStackTrace();
			}
		}
		write(Msg.FAILED);
		return true;
	}

}
