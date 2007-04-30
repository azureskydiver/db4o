/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos.circuits.magnycours;

public interface MagnycoursDriver {

	public void store();

	public void getFirstElement();

	public void getMiddleElement();

	public void getLastElement();

	public void getAllElements();
}
