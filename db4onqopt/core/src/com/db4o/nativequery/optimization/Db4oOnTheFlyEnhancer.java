package com.db4o.nativequery.optimization;

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

	public void optimize(Query query,Predicate filter) {
		try {
			ClassEditor classEditor=new ClassEditor(null,loader.loadClass(filter.getClass().getName()));
			Expression expr=new NativeQueryEnhancer().analyze(bloatUtil,classEditor,Predicate.PREDICATEMETHOD_NAME);
			//System.err.println(expr);
			new SODAQueryBuilder().optimizeQuery(expr,query,filter);
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
}
