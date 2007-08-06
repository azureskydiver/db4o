/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 * @sharpen.partial
 * @sharpen.ignore
 */
public class EmbeddedClientObjectContainer extends PartialEmbeddedClientObjectContainer implements InternalObjectContainer {

    public EmbeddedClientObjectContainer(LocalObjectContainer server) {
        super(server);
    }

    public EmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans) {
        super(server, trans);
    }

}
