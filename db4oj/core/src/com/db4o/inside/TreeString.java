/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.foundation.Tree;
import com.db4o.inside.handlers.*;


/**
 * @exclude
 */
public class TreeString extends Tree {

	public String _key;

	public TreeString(String a_key) {
		this._key = a_key;
	}

	protected Tree shallowCloneInternal(Tree tree) {
		TreeString ts = (TreeString) super.shallowCloneInternal(tree);
		ts._key = _key;
		return ts;
	}

	public Object shallowClone() {
		return shallowCloneInternal(new TreeString(_key));
	}

	public int compare(Tree a_to) {
		return StringHandler
				.compare(Const4.stringIO.write(((TreeString) a_to)._key),
						Const4.stringIO.write(_key));
	}
	
    public Object key(){
    	return _key;
    }

}
