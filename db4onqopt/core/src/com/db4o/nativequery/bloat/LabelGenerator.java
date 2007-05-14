/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.bloat;

import EDU.purdue.cs.bloat.editor.*;

public class LabelGenerator {

	private int _id = 0;

	public Label createLabel(boolean startsBlock) {
		Label label = new Label(_id,startsBlock);
		_id++;
		return label;
	}
}
