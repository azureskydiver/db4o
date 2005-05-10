/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors.db4o;

import com.db4o.ObjectContainer;
import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.binding.dataeditors.IObjectEditorFactory;

public class Db4oObjectEditorFactory implements IObjectEditorFactory {

    private ObjectContainer database;

    public Db4oObjectEditorFactory(ObjectContainer database) {
        this.database = database;
    }
    
    public IObjectEditor construct() {
        return new Db4oObject(database);
    }

}
