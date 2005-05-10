/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors;

/**
 * IObjectEditorFactory. An interface for factory objects that can construct
 * IObjectEditor implementations.
 *
 * @author djo
 */
public interface IObjectEditorFactory {

    /**
     * Method construct.
     * 
     * Construct and return an IObjectEditor according to the application's
     * editing policy.
     * 
     * @return The constructed IObjectEditor.
     */
    IObjectEditor construct();

}
