package com.db4o.reflect.core;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public class ConstructorSupport {
	
	
    public static ReflectConstructorSpec createConstructor(final ConstructorAwareReflectClass claxx, Class clazz, ReflectorConfiguration config, ReflectConstructor[] constructors){
        
        if (claxx == null) {
			throw new ObjectNotStorableException(claxx);
        }
        
        if (claxx.isAbstract() || claxx.isInterface()) {
            return null;
        }

        if(! Platform4.callConstructor()){
    		boolean skipConstructor = !config.callConstructor(claxx);

            if(!claxx.isCollection() && claxx.skipConstructor(skipConstructor, config.testConstructors())){
              return null;
            }
        }
        
        if (! config.testConstructors()) {
          return null;
        }

		if(ReflectPlatform.createInstance(clazz) != null) {
			return null;
		}

		Tree sortedConstructors = sortConstructorsByParamsCount(constructors);
		return findConstructor(claxx, sortedConstructors);
	}

	private static ReflectConstructorSpec findConstructor(final ReflectClass claxx,
			Tree sortedConstructors) {
		if (sortedConstructors == null) {
			throw new ObjectNotStorableException(claxx);
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
				return new ReflectConstructorSpec(constructor, params);
			}
		}
		throw new ObjectNotStorableException(claxx);
	}
	
	private static Tree sortConstructorsByParamsCount(final ReflectConstructor[] constructors) {
		Tree sortedConstructors = null;

		// sort constructors by parameter count
		for (int i = 0; i < constructors.length; i++) {
			int parameterCount = constructors[i].getParameterTypes().length;
			sortedConstructors = Tree.add(sortedConstructors,
					new TreeIntObject(i + constructors.length * parameterCount,
							constructors[i]));
		}
		return sortedConstructors;
	}

}
