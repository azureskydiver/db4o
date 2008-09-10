/**
 * @exclude
 */
package com.db4o.typehandlers;

class KeyValueHandlerPair {
	public final TypeHandler4 _keyHandler;
	public final TypeHandler4 _valueHandler;
	
	public KeyValueHandlerPair(TypeHandler4 keyHandler, TypeHandler4 valueHandler) {
		_keyHandler = keyHandler;
		_valueHandler = valueHandler;
	}
}