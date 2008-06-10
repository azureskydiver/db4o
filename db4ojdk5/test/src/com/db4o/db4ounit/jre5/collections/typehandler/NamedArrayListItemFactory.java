/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;


import db4ounit.fixtures.*;

/**
 * @sharpen.ignore
 */
class NamedArrayListItemFactory extends ItemFactory implements Labeled {
    
    private static class Item {
        public List _list = new NamedArrayList();
    }
    
    public Object newItem() {
        return new Item();
    }

    public Class itemClass() {
        return NamedArrayListItemFactory.Item.class;
    }

    public Class listClass() {
        return NamedArrayList.class;
    }

    public String label() {
        return "NamedArrayList";
    }
}