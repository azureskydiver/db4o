/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


/**
 * @exclude
 * @sharpen.partial
 */
public class EmbeddedClientObjectContainer extends PartialEmbeddedClientObjectContainer implements ExtObjectContainer {

    public EmbeddedClientObjectContainer(LocalObjectContainer server) {
        super(server);
    }

    public EmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans) {
        super(server, trans);
    }

    public ObjectSet query(Predicate predicate, Comparator comparator) throws Db4oIOException,
        DatabaseClosedException {
        // TODO Auto-generated method stub
        return null;
    }

}
