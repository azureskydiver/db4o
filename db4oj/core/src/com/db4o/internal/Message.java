/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.internal.handlers.*;


final class Message
{
	final PrintStream stream;

	Message(ObjectContainerBase a_stream, String msg){
		stream = a_stream.configImpl().outStream();
		print(msg, true);
	}

	Message(String a_StringParam, int a_intParam, PrintStream a_stream, boolean header){
		stream = a_stream;
		print(Messages.get(a_intParam,a_StringParam), header );
	}

	Message(String a_StringParam, int a_intParam, PrintStream a_stream){
		this(a_StringParam, a_intParam , a_stream, true);
	}


	private void print(String msg, boolean header){
		if(stream != null){
			if(header){
				stream.println("[" + Db4o.version() + "   " + DateHandler.now() + "] ");
			}
			stream.println(" " + msg);
		}
	}
}
