/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class LockedTree {
    
    private Tree _tree;
    
    private int _version;

    public void add(Tree tree) {
        changed();
        _tree = _tree == null ? tree : _tree.add(tree); 
    }

    private void changed() {
        _version++;
    }

    public void clear() {
        changed();
        _tree = null;
    }

    public Tree find(int key) {
        return TreeInt.find(_tree, key);
    }

    public void read(Buffer buffer, Readable template) {
        clear();
        _tree = new TreeReader(buffer, template).read();
    }

    public void traverse(Visitor4 visitor) {
        int currentVersion = _version;
        Tree.traverse(_tree, visitor);
        if(_version != currentVersion){
            throw new IllegalStateException();
        }
    }

}
