/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.ext;

import com.db4o.Messages;

/**
 * An old file was detected and could not be open.
 */
public class OldFormatException extends Db4oException {
	public OldFormatException() {
		super(Messages.OLD_DATABASE_FORMAT);
	}
}
