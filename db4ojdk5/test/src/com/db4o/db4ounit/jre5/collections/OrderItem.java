package com.db4o.db4ounit.jre5.collections;

import com.db4o.activation.Activator;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.ta.*;

public class OrderItem implements Activatable {
	private Product _product;
	private int _quantity;
	
	public OrderItem(Product product, int quantity) {
		_product = product;
		_quantity = quantity; 
	}
	
	public Product product() {
		activate();
		return _product;
	}
	
	public int quantity() {
		activate();
		return _quantity;
	}
	
	public void activate()
	{
		if (_activator != null) _activator.activate();
	}

	public void bind(Activator activator) {
		if (activator == null || _activator != null) throw new ArgumentNullException();
		_activator = activator;
	}

	private transient Activator _activator;
}
