package com.db4o.db4ounit.jre5.collections;

import com.db4o.activation.Activator;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.ta.*;

public class Product implements Activatable {
	private String _code;
	private String _description;
	
	public Product(String code, String description) {
		_code = code;
		_description = description;
	}
	
	public String code() {
		activate();
		return _code;
	}
	
	public String description() {
		activate();
		return _description;
	}
	
	public boolean equals(Object p) {
		activate();
		
		if (p == null) return false;
		if (p.getClass() != this.getClass()) return false;
		
		Product rhs = (Product) p;
		return  rhs._code == _code;
	}

	public void activate()
	{
		if (_activator != null) {
			_activator.activate();
		}
	}

	public void bind(Activator activator) {
		if (activator == null || _activator != null) throw new ArgumentNullException();
		_activator = activator;
	}
	
	private transient Activator _activator;
}
