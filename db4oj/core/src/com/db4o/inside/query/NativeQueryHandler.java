package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

public class NativeQueryHandler {
	
	private static final String OPTIMIZER_IMPL_NAME = "com.db4o.nativequery.optimization.Db4oOnTheFlyEnhancer";

	public final static String UNOPTIMIZED = "UNOPTIMIZED";
	public final static String PREOPTIMIZED = "PREOPTIMIZED";
	public final static String DYNOPTIMIZED = "DYNOPTIMIZED";
	
	private ObjectContainer _container;
	private Db4oNQOptimizer _enhancer;
	private List4 _listeners;
	
	public NativeQueryHandler(ObjectContainer container) {
		_container = container;
		loadQueryOptimizer();
    }	

	public void addListener(Db4oQueryExecutionListener listener) {
		_listeners=new List4(_listeners,listener);
	}

	public void clearListeners() {
		_listeners=null;
	}
	
	public ObjectSet execute(Predicate predicate) {
		return configureQuery(predicate).execute();
	}
	
	private Query configureQuery(Predicate predicate) {
		Query q=_container.query();
		q.constrain(predicate.extentType());
		if(predicate instanceof Db4oEnhancedFilter) {
			((Db4oEnhancedFilter)predicate).optimizeQuery(q);
			notifyListeners(predicate,NativeQueryHandler.PREOPTIMIZED);
			return q;
		}
		try {
			if (_container.ext().configure().optimizeNativeQueries() && _enhancer!=null) {
				_enhancer.optimize(q,predicate);
				notifyListeners(predicate,NativeQueryHandler.DYNOPTIMIZED);
				return q;
			}
		} catch (Exception exc) {
			//exc.printStackTrace();
		}
		q.constrain(new PredicateEvaluation(predicate));
		notifyListeners(predicate,NativeQueryHandler.UNOPTIMIZED);
		return q;
	}

	private void notifyListeners(Predicate predicate, String msg) {
		for(Iterator4 iter=new Iterator4Impl(_listeners);iter.hasNext();/**/) {
			((Db4oQueryExecutionListener)iter.next()).notifyQueryExecuted(predicate,msg);
		}
	}
	
	private void loadQueryOptimizer() {
		try {
			Class enhancerClass = Class.forName(NativeQueryHandler.OPTIMIZER_IMPL_NAME);
			_enhancer=(Db4oNQOptimizer)enhancerClass.newInstance();
		} catch (Throwable ignored) {
			_enhancer=null;
		}
	}
}
