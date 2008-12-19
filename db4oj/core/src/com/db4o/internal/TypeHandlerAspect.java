/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class TypeHandlerAspect extends ClassAspect {
    
    public final TypeHandler4 _typeHandler;
    
    public TypeHandlerAspect(TypeHandler4 typeHandler){
        _typeHandler = typeHandler;
    }
    
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null || obj.getClass() != getClass()){
            return false;
        }
        TypeHandlerAspect other = (TypeHandlerAspect) obj;
        return _typeHandler.equals(other._typeHandler);
    }
    
    public int hashCode() {
        return _typeHandler.hashCode();
    }

    public String getName() {
        return _typeHandler.getClass().getName();
    }

    public void cascadeActivation(Transaction trans, Object obj, ActivationDepth depth) {
    	if(! Handlers4.isFirstClass(_typeHandler)){
    		return;
    	}
    	ActivationContext4 context = new ActivationContext4(trans, obj, depth);
    	Handlers4.cascadeActivation(context, _typeHandler);
    }

    public void collectIDs(final CollectIdContext context) {
    	if(! Handlers4.isFirstClass(_typeHandler)){
    		incrementOffset(context);
    		return;
    	}
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
		    	QueryingReadContext queryingReadContext = new QueryingReadContext(context.transaction(), context.handlerVersion(), context.buffer(), 0, context.collector());
		    	((FirstClassHandler)_typeHandler).collectIDs(queryingReadContext);
				return null;
			}
    	});
    }

    public void defragAspect(final DefragmentContext context) {
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
				_typeHandler.defragment(context);
				return null;
			}
		
		});
    }

    public int linkLength() {
        return Const4.INDIRECTION_LENGTH;
    }

    public void marshall(MarshallingContext context, Object obj) {
    	context.createIndirectionWithinSlot();
        _typeHandler.write(context, obj);
    }

    public AspectType aspectType() {
        return AspectType.TYPEHANDLER;
    }

    public void instantiate(final UnmarshallingContext context) {
    	if(! checkEnabled(context)){
    		return;
    	}
    	final Object oldObject = context.persistentObject();
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
		        Object readObject = _typeHandler.read(context);
		        if(readObject != null && oldObject != readObject){
		        	context.persistentObject(readObject);
		        }
				return null;
			}
		});
    }

	public void delete(final DeleteContextImpl context, boolean isUpdate) {
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
				_typeHandler.delete(context);
				return null;
			}
		});
	}

	public void deactivate(Transaction trans, Object obj, ActivationDepth depth) {
		cascadeActivation(trans, obj, depth);
	}

	public boolean canBeDisabled() {
		return true;
	}

}
