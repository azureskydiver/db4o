package com.db4o.db4ounit.jre5.collections;

import com.db4o.activation.*;
import com.db4o.collections.ArrayList4;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.ta.Activatable;

public class Order implements Activatable {
	private ArrayList4<OrderItem> _items;

	public Order() {
		_items = new ArrayList4<OrderItem>();
	}
	
	public void addItem(OrderItem item) {
		activate(ActivationPurpose.READ);
		_items.add(item);
	}
	
	public OrderItem item(int i) {
		activate(ActivationPurpose.READ);
		return _items.get(i);
	}

	public int size() {
		activate(ActivationPurpose.READ);
		return _items.size();
	}

	public void activate(ActivationPurpose purpose)
	{
		if (_activator != null) _activator.activate(purpose);
	}

	public void bind(Activator activator) {
		if (activator == null || _activator != null) throw new ArgumentNullException();
		_activator = activator;
	}
	
	private transient Activator _activator;
}