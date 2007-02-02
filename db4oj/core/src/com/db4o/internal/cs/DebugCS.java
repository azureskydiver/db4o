/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class DebugCS {

	public static ClientObjectContainer clientStream;
	public static LocalObjectContainer serverStream;
	public static Queue4 clientMessageQueue;
	public static Lock4 clientMessageQueueLock;

}
