package db4ounit.extensions.util;

import com.db4o.internal.ReflectPlatform;

public class CrossPlatformServices {

	public static String simpleName(String typeName) {
		int index = typeName.indexOf(',');
		if (index < 0) return typeName;
		return typeName.substring(0, index);
	}

	public static String fullyQualifiedName(Class klass) {
		return ReflectPlatform.fullyQualifiedName(klass);
	}

}
