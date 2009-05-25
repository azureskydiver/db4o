package com.db4o.foundation;


public class Pair<TFirst, TSecond> {
	
	public static <TFirst, TSecond> Pair<TFirst, TSecond> of(TFirst first, TSecond second) {
		return new Pair(first, second);
	}
	
	public TFirst first;
	public TSecond second;
	
	public Pair(TFirst first, TSecond second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "Pair.of(" + first + ", " + second + ")";
	}

}
