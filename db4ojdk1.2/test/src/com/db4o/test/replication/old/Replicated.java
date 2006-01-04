/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication.old;

class Replicated {

    String _name;

    Replicated(String name) {
        _name = name;
    }

    public String toString() {
        return _name;
    }
}
