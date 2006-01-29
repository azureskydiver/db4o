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
			ClassEditor classEditor=new ClassEditor(context,loader.loadClass(filter.getClass().getName()));
			Expression expr=new NativeQueryEnhancer().analyze(bloatUtil,classEditor,Predicate.PREDICATEMETHOD_NAME);
			//System.err.println(expr);
			if(expr==null) {
				throw new RuntimeException();
			}
			new SODAQueryBuilder().optimizeQuery(expr,query,filter);
			return expr;
		} catch (ClassNotFoundException exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
}
