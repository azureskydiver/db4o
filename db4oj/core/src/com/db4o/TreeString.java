/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * @exclude
 */
public class TreeString extends Tree{
    
	public final String i_key;
	
	public TreeString(String a_key){
		this.i_key = a_key;
	}

    int compare(Tree a_to) {
        return YapString.compare(YapConst.stringIO.write(((TreeString)a_to).i_key), YapConst.stringIO.write(i_key));
    }

}
