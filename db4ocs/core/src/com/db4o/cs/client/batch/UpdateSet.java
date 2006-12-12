package com.db4o.cs.client.batch;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Dec 11, 2006
 * Time: 5:38:26 PM
 */
public class UpdateSet {
	List<FieldValue> updatesToApply = new ArrayList<FieldValue>();

	public void add(FieldValue fieldValue) {
		updatesToApply.add(fieldValue);
	}
}
