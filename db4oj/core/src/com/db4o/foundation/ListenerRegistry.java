/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class ListenerRegistry <E>{
	
	public static <E> ListenerRegistry<E> newInstance() {
		return new ListenerRegistry<E>();
	}

	private Collection4 _listeners;
	
	public void register(Listener<E> listener){
		if(_listeners == null){
			_listeners = new Collection4();
		}
		_listeners.add(listener);
	}
	
	public void notifyListeners(E event){
		if(_listeners == null){
			return;
		}
		Iterator4 i = _listeners.iterator();
		while(i.moveNext()){
			((Listener)i.current()).onEvent(event);
		}
	}
}
