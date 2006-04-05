package com.db4o.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.db4o.ObjectSet;
import com.db4o.foundation.Cool;
import com.db4o.foundation.Hashtable4;
import com.db4o.inside.replication.ReplicationReference;


public class ImplementationComparator implements InvocationHandler {

	private final Object _hot;
	private final Object _backup;
	private final Hashtable4 _ignoredMethods = new Hashtable4(10);

	private final Object _hotProxy;
	private final Object _backupProxy;
	
	private boolean _hotResultReady = false;
	private String _hotMethodCalled;
	private Object _hotResult;


	public ImplementationComparator(Object hot, Object backup, String[] ignoredMethods) {
		_hot = hot;
		_backup = backup;

		_hotProxy = proxyFor(_hot);
		_backupProxy = proxyFor(_backup);
		
		for (int i = 0; i < ignoredMethods.length; i++) {
			_ignoredMethods.put(ignoredMethods[i], ignoredMethods[i]); //Using it as a Set.
		}
	}

	
	private Object proxyFor(Object obj) {
		Class clazz = obj.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}


	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (proxy == _hotProxy) return invokeOnHot(method, args);
		if (proxy == _backupProxy) return invokeOnBackup(method, args);
		throw new RuntimeException();
	}
	
	synchronized private Object invokeOnBackup(Method method, Object[] args) throws Throwable {
		System.out.println("> > > > > > > > > B " + method.getName());
		if (!_hotResultReady) wait();
		_hotResultReady = false;
		notify();


		Cool.sleepIgnoringInterruption(1000);
		
		
		Object result = invoke2(_backup, method, args);
		if (!_hotMethodCalled.equals(method.getName()))
			throw new RuntimeException("Different method called. Hot: " + _hotMethodCalled + "  Backup: " + method.getName());
		if (!compare(_hotResult, result))
			handleDifference(method, _hotResult, result);

		return throwIfException(result);
	}


	synchronized private Object invokeOnHot(Method method, Object[] args) throws Throwable {
		System.out.println("> > > > > > > > > H " + method.getName());
		
		if (method.getName().equals("hasReplicationReferenceAlready")) new RuntimeException().printStackTrace();
		
		if (_hotResultReady) wait();
		_hotResultReady = true;
		notify();

		
		Cool.sleepIgnoringInterruption(1000);

		
		_hotMethodCalled = method.getName();
		_hotResult = invoke2(_hot, method, args);
	
		return throwIfException(_hotResult);
	}


	private Object throwIfException(Object result) throws Throwable {
		if (result instanceof InvocationTargetException)
			throw ((InvocationTargetException)result).getTargetException();

		return result;
	}

	
	public boolean compare(Object hotResult, Object backupResult) throws Throwable {

		if (backupResult == null) return hotResult == null;

		if (hotResult instanceof ObjectSet)
			return haveEqualContents((ObjectSet)hotResult, (ObjectSet)backupResult);

		if (hotResult instanceof ReplicationReference)
			return sameReference((ReplicationReference)hotResult, (ReplicationReference)backupResult);

		return backupResult.equals(hotResult);
	}

	private boolean sameReference(ReplicationReference ref, ReplicationReference ref2) {
		if (!ref.uuid().equals(ref2.uuid())) return false;
		return ref.object().equals(ref2.object());
	}

	private boolean haveEqualContents(ObjectSet set, ObjectSet set2) {
		if (set.size() != set2.size()) return false;
		try {
			while (set.hasNext())
				if (!set.next().equals(set2.next())) return false;
			return true;
		} finally {
			set.reset();
			set2.reset();
		}
	}

	private void handleDifference(Method method, Object hotResult, Object backupResult) {
		if (_ignoredMethods.get(method.getName()) != null) return;
		printIfInvocationTargetException(hotResult);
		printIfInvocationTargetException(backupResult);
		
		RuntimeException rx = new RuntimeException("Different results for method " + method.getName() + ".  Hot: " + hotResult + "  Backup: " + backupResult);
		rx.printStackTrace();
		throw rx;
	}

	private void printIfInvocationTargetException(Object obj) {
		if (obj instanceof InvocationTargetException)
			((InvocationTargetException)obj).getTargetException().printStackTrace();
	}

	private Object invoke2(Object obj, Method method, Object[] args) throws Exception {
		try {
			return method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			return e;
		}
	}

	public Object hotProxy() {
		return _hotProxy;
	}

	public Object backupProxy() {
		return _backupProxy;
	}
	
}
