package com.db4o.nativequery.optimization;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.inside.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.query.*;

// only introduced to keep Db4oListFacade clean of Bloat references
public class Db4oOnTheFlyEnhancer implements Db4oNQOptimizer {
	private transient ClassFileLoader loader=new ClassFileLoader();

	public void optimize(Query query,Predicate filter) {
		try {
			ClassEditor classEditor=new ClassEditor(null,loader.loadClass(filter.getClass().getName()));
			Expression expr=new NativeQueryEnhancer().analyze(loader,classEditor,Predicate.PREDICATEMETHOD_NAME);
			new SODAQueryBuilder().optimizeQuery(expr,query,filter);
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
}
