package com.db4o.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.db4o.ObjectSet;
import com.db4o.foundation.Hashtable4;
import com.db4o.inside.replication.ReplicationReference;


public class ImplementationComparator implements InvocationHandler {

	private final Object _hot;
	private final Object _backup;
	private final Hashtable4 _ignoredMethods = new Hashtable4(10);


	public static Object createGiven(Object hot, Object backup, String[] ignoredMethods) {
		ImplementationComparator handler = new ImplementationComparator(hot, backup, ignoredMethods);
		return handler.proxyInstance();
	}

	public ImplementationComparator(Object hot, Object backup, String[] ignoredMethods) {
		_hot = hot;
		_backup = backup;
		for (int i = 0; i < ignoredMethods.length; i++) {
			_ignoredMethods.put(ignoredMethods[i], ignoredMethods[i]); //Using it as a Set.
		}
	}

	private Object proxyInstance() {
		return Proxy.newProxyInstance(_hot.getClass().getClassLoader(), interfaces(), this);
	}

	private Class<?>[] interfaces() {
		return _hot.getClass().getInterfaces();
	}

	public Object invoke(Object hot, Method method, Object[] args) throws Throwable {
		Object hotResult = invoke2(_hot, method, args);
		Object backupResult = invoke2(_backup, method, args);

		if (backupResult == null) {
			if (hotResult == null) return null;
			handleDifference(method, hotResult, backupResult);
			return null;
		}

		if (hotResult instanceof ObjectSet) {
			if (!haveEqualContents((ObjectSet)hotResult, (ObjectSet)backupResult))
				handleDifference(method, hotResult, backupResult);
			return hotResult;
		}

		if (hotResult instanceof ReplicationReference) {
			if (!sameReference((ReplicationReference)hotResult, (ReplicationReference)backupResult))
				handleDifference(method, hotResult, backupResult);
			return hotResult;
		}

		if (!backupResult.equals(hotResult)) 
			handleDifference(method, hotResult, backupResult);
		
		if (hotResult instanceof InvocationTargetException)
			throw ((InvocationTargetException)hotResult).getTargetException();
		
		return hotResult;
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
		throw new RuntimeException("Different results for method " + method.getName() + ".  Hot: " + hotResult + "  Backup: " + backupResult);
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
	
}
