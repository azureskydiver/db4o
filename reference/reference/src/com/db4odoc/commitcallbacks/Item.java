/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.commitcallbacks;


public class Item {
	private int _number;
	private String _word;
	
	public Item(int number, String word){
		_number = number;
		_word = word;
	}
	
	public String getWord(){
		return _word;
	}
	
	public int getNumber(){
		return _number;
	}
	
	public String toString(){
		return _number + "/" + _word;
	}
}
