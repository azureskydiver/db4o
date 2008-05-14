package profiling;

import java.lang.reflect.*;

import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

public class CustomItemClass extends JdkClass {

	public CustomItemClass(Reflector reflector, CustomItemReflector customItemReflector) {
		super(reflector, customItemReflector, Item.class);
	}
	
	@Override
	protected JdkField createField(Field field) {
		if (field.getName().equals("_intValue")) {
			return new JdkField(_reflector, field) {
				@Override
				public Object get(Object onObject) {
					return ((Item)onObject)._intValue;
				}
				
				@Override
				public void set(Object onObject, Object attribute) {
					((Item)onObject)._intValue = (Integer)attribute;
				}
			};
		}
		
		if (field.getName().equals("_stringValue")) {
			return new JdkField(_reflector, field) {
				@Override
				public Object get(Object onObject) {
					return ((Item)onObject)._stringValue;
				}
				
				@Override
				public void set(Object onObject, Object attribute) {
					((Item)onObject)._stringValue = (String)attribute;
				}
			};
		}
		return super.createField(field);
	}

}
