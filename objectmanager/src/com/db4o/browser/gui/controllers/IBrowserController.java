/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import com.db4o.browser.model.IGraphIterator;

/**
 * IBrowserController.
 *
 * @author djo
 */
public interface IBrowserController {
	public void setInput(IGraphIterator input);
}
