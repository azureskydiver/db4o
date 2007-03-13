/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.reflect.generic.GenericClass;

public class MClassMeta extends MsgObject {
	public boolean processAtServer(ServerMessageDispatcher serverThread) {
		ObjectContainerBase stream = stream();
		unmarshall();
		try{
			ClassInfo classMeta = (ClassInfo) stream().unmarshall(_payLoad);
			GenericClass genericClass = stream.getClassMetaHelper().classMetaToGenericClass(stream().reflector(), classMeta);
			if (genericClass != null) {
				synchronized (streamLock()) {
					Transaction trans = stream.getSystemTransaction();
	
					ClassMetadata yapClass = stream.produceClassMetadata(genericClass);
					if (yapClass != null) {
						stream.checkStillToSet();
						yapClass.setStateDirty();
						yapClass.write(trans);
						trans.commit();
						StatefulBuffer returnBytes = stream
								.readWriterByID(trans, yapClass.getID());
						serverThread.write(Msg.OBJECT_TO_CLIENT.getWriter(returnBytes));
						return true;
					}
				}
			}
		}catch(Exception e){
			if(Debug.atHome){
				e.printStackTrace();
			}
		}
		serverThread.write(Msg.FAILED);
		return true;
	}

}
