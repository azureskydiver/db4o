/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;

final class EventDispatcher
{
	private static final String[] events = {
		"objectCanDelete",
		"objectOnDelete", 
		"objectOnActivate", 
		"objectOnDeactivate",
		"objectOnNew",
		"objectOnUpdate",
		"objectCanActivate",
		"objectCanDeactivate",
		"objectCanNew",
		"objectCanUpdate"
	};
	
	static final int CAN_DELETE = 0;
	static final int DELETE = 1;
	static final int SERVER_COUNT = 2;
	static final int ACTIVATE = 2;
	static final int DEACTIVATE = 3;
	static final int NEW = 4;
	static final int UPDATE = 5;
	static final int CAN_ACTIVATE = 6;
	static final int CAN_DEACTIVATE = 7;
	static final int CAN_NEW = 8;
	static final int CAN_UPDATE = 9;
	static final int COUNT = 10;
	
	private final IMethod[] methods;
	
	private EventDispatcher(IMethod[] methods){
		this.methods = methods;
	}
	
	boolean dispatch(YapStream stream, Object obj, int eventID){
		if(methods[eventID] != null){
			Object[] parameters = new Object[]{stream};
			try{
				Object res = methods[eventID].invoke(obj,parameters);
				if(res != null && res instanceof Boolean){
				    return ((Boolean)res).booleanValue();
				}
			}catch(Throwable t){
			}
		}
		return true;
	}
	
	static EventDispatcher forClass(YapStream a_stream, IClass classReflector){
		EventDispatcher dispatcher = null;
		if(a_stream != null){
		    int count = 0;
		    if(a_stream.i_config.i_callbacks){
		        count = COUNT;
		    }else if(a_stream.i_config.i_isServer){
		        count = SERVER_COUNT;
		    }
		    if(count > 0){
				Class[] parameterClasses = {YapConst.CLASS_OBJECTCONTAINER};
				IMethod[] methods = new IMethod[COUNT];
				for (int i = COUNT -1; i >=0; i--){
					try{
						methods[i] = classReflector.getMethod(events[i], parameterClasses);
						if(dispatcher == null){
							dispatcher = new EventDispatcher(methods);
						}
					}catch(Throwable t){}
				}
		    }
		}
		return dispatcher;
	}
}
