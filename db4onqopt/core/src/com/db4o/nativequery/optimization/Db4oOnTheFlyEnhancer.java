/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.inside.query.*;
import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.expr.*;
import com.db4o.query.*;

// only introduced to keep Db4oListFacade clean of Bloat references
public class Db4oOnTheFlyEnhancer implements Db4oNQOptimizer {
	private transient ClassFileLoader loader=new ClassFileLoader();
	private transient BloatUtil bloatUtil=new BloatUtil(loader);
	private transient EditorContext context=new CachingBloatContext(loader,new ArrayList(),false);
	
	public Object optimize(Query query,Predicate filter) {
		try {
			//long start=System.currentTimeMillis();
			Expression expr = analyzeInternal(filter);
			//System.err.println((System.currentTimeMillis()-start)+" ms");
			//System.err.println(expr);
			if(expr==null) {
				throw new RuntimeException();
			}
			//start=System.currentTimeMillis();
			new SODAQueryBuilder().optimizeQuery(expr,query,filter);
			//System.err.println((System.currentTimeMillis()-start)+" ms");
			return expr;
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}

	private Expression analyzeInternal(Predicate filter) throws ClassNotFoundException {
		ClassEditor classEditor=new ClassEditor(context,loader.loadClass(filter.getClass().getName()));
		Expression expr=new NativeQueryEnhancer().analyze(bloatUtil,classEditor,Predicate.PREDICATEMETHOD_NAME);
		return expr;
	}
	
	public static Expression analyze(Predicate filter) throws ClassNotFoundException {
		return new Db4oOnTheFlyEnhancer().analyzeInternal(filter);
	}
}
