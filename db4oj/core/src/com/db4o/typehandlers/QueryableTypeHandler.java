package com.db4o.typehandlers;

public interface QueryableTypeHandler extends TypeHandler4 {
	
	/**
	 * Returns true if the types handled by this type handler can not refer to other objects.
	 */
    public boolean isSimple();


}
