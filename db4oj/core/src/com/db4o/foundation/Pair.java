package com.db4o.foundation;

public class Pair<TFirst, TSecond> {
	
	public final TFirst first;
	public final TSecond second;
	
	public Pair(TFirst first, TSecond second) {
		this.first = first;
		this.second = second;
	}

}
