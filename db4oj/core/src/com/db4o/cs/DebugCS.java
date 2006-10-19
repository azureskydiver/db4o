/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs;

import com.db4o.*;
import com.db4o.foundation.*;


/**
 * @exclude
 */
public class DebugCS {

	public static YapClient clientStream;
	public static YapFile serverStream;
	public static Queue4 clientMessageQueue;
	public static Lock4 clientMessageQueueLock;

}
