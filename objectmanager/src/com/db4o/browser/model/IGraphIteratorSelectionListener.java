/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;


/**
 * IGraphIteratorSelectionListener. An interface for objects that need to 
 * be notified with an IGraphIterator's selection changes.
 *
 * @author djo
 */
public interface IGraphIteratorSelectionListener {
	void selectionChanged();
}