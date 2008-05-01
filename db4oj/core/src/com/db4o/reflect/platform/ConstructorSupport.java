package com.db4o.reflect.platform;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public class ConstructorSupport {
	
	
    public static boolean createConstructor(final ReflectClass claxx, ReflectorConfiguration config, boolean skipConstructor){
        
        if (claxx == null) {
            return false;
        }
        
        if (claxx.isAbstract() || claxx.isInterface()) {
            return true;
        }
        
        if(! Platform4.callConstructor()){
            if(claxx.skipConstructor(skipConstructor, config.testConstructors())){
                return true;
            }
        }
        
        if (! config.testConstructors()) {
            return true;
        }
        
        if (claxx.newInstance() != null) {
            return true;
        }
        
        if (claxx.reflector().constructorCallsSupported()) {
			Tree sortedConstructors = sortConstructorsByParamsCount(claxx);
			return findConstructor(claxx, sortedConstructors);
		}
		return false;
	}

	private static boolean findConstructor(final ReflectClass claxx,
			Tree sortedConstructors) {
		if (sortedConstructors == null) {
			return false;
		}
		
		Iterator4 iter = new TreeNodeIterator(sortedConstructors);
		while (iter.moveNext()) {
			Object obj = iter.current();
			ReflectConstructor constructor = (ReflectConstructor) ((TreeIntObject) obj)._object;
			ReflectClass[] paramTypes = constructor.getParameterTypes();
			Object[] params = new Object[paramTypes.length];
			for (int j = 0; j < params.length; j++) {
				params[j] = paramTypes[j].nullValue();
			}
			Object res = constructor.newInstance(params);
			if (res != null) {
				claxx.useConstructor(constructor, params);
				return true;
			}
		}
		return false;
	}
	
	private static Tree sortConstructorsByParamsCount(final ReflectClass claxx) {
		ReflectConstructor[] constructors = claxx.getDeclaredConstructors();

		Tree sortedConstructors = null;

		// sort constructors by parameter count
		for (int i = 0; i < constructors.length; i++) {
			constructors[i].setAccessible();
			int parameterCount = constructors[i].getParameterTypes().length;
			sortedConstructors = Tree.add(sortedConstructors,
					new TreeIntObject(i + constructors.length * parameterCount,
							constructors[i]));
		}
		return sortedConstructors;
	}

}
