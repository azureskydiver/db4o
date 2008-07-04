/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.config;

import com.db4o.*;
import com.db4o.foundation.ChainedRuntimeException;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.List4;
import com.db4o.internal.*;
import com.db4o.internal.cs.ClassInfo;
import com.db4o.internal.cs.FieldInfo;
import com.db4o.internal.cs.messages.MUserMessage;
import com.db4o.internal.handlers.*;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;

/**
 * Adds the basic configuration settings required to access a
 * .net generated database from java.
 * 
 * The configuration only makes sure that database files can be
 * successfully open and things like UUIDs can be successfully
 * retrieved.
 * 
 * @sharpen.ignore
 */
public class DotnetSupport implements ConfigurationItem {

	private final boolean _addCSSupport;
	
	public DotnetSupport() {
		_addCSSupport = false;	
	}
	
	/**
	 * @param addCSSupport true if mappings required for Client/Server 
	 *                     support should be included also.
	 */
	public DotnetSupport(boolean addCSSupport) {
		_addCSSupport = addCSSupport;
	}

	public void prepare(Configuration config) {
		config.addAlias(new WildcardAlias("Db4objects.Db4o.Ext.*, Db4objects.Db4o", "com.db4o.ext.*"));		
		config.addAlias(new TypeAlias("Db4objects.Db4o.StaticField, Db4objects.Db4o", StaticField.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.StaticClass, Db4objects.Db4o", StaticClass.class.getName()));
		
		if (_addCSSupport) {
			config.addAlias(new TypeAlias("System.Exception, mscorlib", ChainedRuntimeException.class.getName()));
			
	//		config.addAlias(new TypeAlias("java.lang.Throwable", FullTypeNameFor(typeof(Exception))));
	//		config.addAlias(new TypeAlias("java.lang.RuntimeException", FullTypeNameFor(typeof(Exception))));
	//		config.addAlias(new TypeAlias("java.lang.Exception", FullTypeNameFor(typeof(Exception))));
	
	
			config.addAlias(new TypeAlias("Db4objects.Db4o.Query.IEvaluation, Db4objects.Db4o", Evaluation.class.getName()));
			config.addAlias(new TypeAlias("Db4objects.Db4o.Query.ICandidate, Db4objects.Db4o", Candidate.class.getName()));
	
			config.addAlias(new WildcardAlias("Db4objects.Db4o.Internal.Query.Processor.*, Db4objects.Db4o", "com.db4o.internal.query.processor.*"));
	
			config.addAlias(new TypeAlias("Db4objects.Db4o.Foundation.Collection4, Db4objects.Db4o", Collection4.class.getName()));
			config.addAlias(new TypeAlias("Db4objects.Db4o.Foundation.List4, Db4objects.Db4o", List4.class.getName()));
			config.addAlias(new TypeAlias("Db4objects.Db4o.User, Db4objects.Db4o", User.class.getName()));
	
			config.addAlias(new TypeAlias("Db4objects.Db4o.Internal.CS.ClassInfo, Db4objects.Db4o", ClassInfo.class.getName()));
			config.addAlias(new TypeAlias("Db4objects.Db4o.Internal.CS.FieldInfo, Db4objects.Db4o", FieldInfo.class.getName()));
	
			config.addAlias(
					new TypeAlias(
							"Db4objects.Db4o.Internal.CS.Messages.MUserMessage+UserMessagePayload, Db4objects.Db4o", 
							MUserMessage.UserMessagePayload.class.getName()));
			
			config.addAlias(new WildcardAlias("Db4objects.Db4o.Internal.CS.Messages.*, Db4objects.Db4o", "com.db4o.internal.cs.messages.*"));
		}
	}
	
	public void apply(InternalObjectContainer container) {
		NetTypeHandler[] handlers = Platform4.jdk().netTypes(container.reflector());
		for (int netTypeIdx = 0; netTypeIdx < handlers.length; netTypeIdx++) {
			NetTypeHandler handler = handlers[netTypeIdx];
			container.handlers().registerNetTypeHandler(handler);
		}
	}
}
