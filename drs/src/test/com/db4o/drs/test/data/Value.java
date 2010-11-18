package com.db4o.drs.test.data;

/**
 * @sharpen.struct
 */
public class Value
{
	private int value;
	
	public Value(int value) {
		this.setValue(value);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Value)) {
			return false;
		}
		Value other = (Value)obj;
		return other.getValue() == getValue();
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}