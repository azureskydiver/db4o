/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors;

/**
 * IObjectListener.  An interface for objects that listen to state change
 * events in an edited object.
 *
 * @author djo
 */
public interface IObjectListener {
    /**
     * Called whenever the edited object's state changes.
     * 
     * @param sender The IObjectEditor that is managing editing for this object
     */
    public void stateChanged(IObjectEditor sender);
}
