/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

public final class StringBufferHandler implements TypeHandler4, BuiltinTypeHandler,
        SecondClassTypeHandler, VariableLengthTypeHandler, EmbeddedTypeHandler {

    private ReflectClass _classReflector;
    
    public void defragment(DefragmentContext context) {
        stringHandler(context).defragment(context);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        stringHandler(context).delete(context);
    }

    public Object read(ReadContext context) {
        Object read = stringHandler(context).read(context);
        if (null == read) {
            return null;
        }
        return new StringBuffer((String) read);
    }

    public void write(WriteContext context, Object obj) {
        stringHandler(context).write(context, obj.toString());
    }

    private TypeHandler4 stringHandler(Context context) {
        return handlers(context)._stringHandler;
    }

    private HandlerRegistry handlers(Context context) {
        return ((InternalObjectContainer) context.objectContainer()).handlers();
    }

    public PreparedComparison prepareComparison(Context context, Object obj) {
        return stringHandler(context).prepareComparison(context, obj);
    }

    /*
     * @see com.db4o.internal.BuiltinTypeHandler#classReflector(com.db4o.reflect.Reflector)
     */
    public ReflectClass classReflector() {
        return _classReflector;
    }

	public void registerReflector(Reflector reflector) {
        _classReflector = reflector.forClass(StringBuffer.class);
	}
}