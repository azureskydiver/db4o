namespace com.db4o.@internal.cs
{
	public class ClassInfoHelper
	{
		private com.db4o.foundation.Hashtable4 _classMetaTable = new com.db4o.foundation.Hashtable4
			();

		private com.db4o.foundation.Hashtable4 _genericClassTable = new com.db4o.foundation.Hashtable4
			();

		public virtual com.db4o.@internal.cs.ClassInfo GetClassMeta(com.db4o.reflect.ReflectClass
			 claxx)
		{
			string className = claxx.GetName();
			if (IsSystemClass(className))
			{
				return com.db4o.@internal.cs.ClassInfo.NewSystemClass(className);
			}
			com.db4o.@internal.cs.ClassInfo existing = LookupClassMeta(className);
			if (existing != null)
			{
				return existing;
			}
			return NewUserClassMeta(claxx);
		}

		private com.db4o.@internal.cs.ClassInfo NewUserClassMeta(com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.@internal.cs.ClassInfo classMeta = com.db4o.@internal.cs.ClassInfo.NewUserClass
				(claxx.GetName());
			classMeta.SetSuperClass(MapSuperclass(claxx));
			RegisterClassMeta(claxx.GetName(), classMeta);
			classMeta.SetFields(MapFields(claxx.GetDeclaredFields()));
			return classMeta;
		}

		private com.db4o.@internal.cs.ClassInfo MapSuperclass(com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.reflect.ReflectClass superClass = claxx.GetSuperclass();
			if (superClass != null)
			{
				return GetClassMeta(superClass);
			}
			return null;
		}

		private com.db4o.@internal.cs.FieldInfo[] MapFields(com.db4o.reflect.ReflectField[]
			 fields)
		{
			com.db4o.@internal.cs.FieldInfo[] fieldsMeta = new com.db4o.@internal.cs.FieldInfo
				[fields.Length];
			for (int i = 0; i < fields.Length; ++i)
			{
				com.db4o.reflect.ReflectField field = fields[i];
				bool isArray = field.GetFieldType().IsArray();
				com.db4o.reflect.ReflectClass fieldClass = isArray ? field.GetFieldType().GetComponentType
					() : field.GetFieldType();
				bool isPrimitive = fieldClass.IsPrimitive();
				fieldsMeta[i] = new com.db4o.@internal.cs.FieldInfo(field.GetName(), GetClassMeta
					(fieldClass), isPrimitive, isArray, false);
			}
			return fieldsMeta;
		}

		private static bool IsSystemClass(string className)
		{
			return className.StartsWith("java");
		}

		private com.db4o.@internal.cs.ClassInfo LookupClassMeta(string className)
		{
			return (com.db4o.@internal.cs.ClassInfo)_classMetaTable.Get(className);
		}

		private void RegisterClassMeta(string className, com.db4o.@internal.cs.ClassInfo 
			classMeta)
		{
			_classMetaTable.Put(className, classMeta);
		}

		public virtual com.db4o.reflect.generic.GenericClass ClassMetaToGenericClass(com.db4o.reflect.generic.GenericReflector
			 reflector, com.db4o.@internal.cs.ClassInfo classMeta)
		{
			if (classMeta.IsSystemClass())
			{
				return (com.db4o.reflect.generic.GenericClass)reflector.ForName(classMeta.GetClassName
					());
			}
			string className = classMeta.GetClassName();
			com.db4o.reflect.generic.GenericClass genericClass = LookupGenericClass(className
				);
			if (genericClass != null)
			{
				return genericClass;
			}
			com.db4o.reflect.generic.GenericClass genericSuperClass = null;
			com.db4o.@internal.cs.ClassInfo superClassMeta = classMeta.GetSuperClass();
			if (superClassMeta != null)
			{
				genericSuperClass = ClassMetaToGenericClass(reflector, superClassMeta);
			}
			genericClass = new com.db4o.reflect.generic.GenericClass(reflector, null, className
				, genericSuperClass);
			RegisterGenericClass(className, genericClass);
			com.db4o.@internal.cs.FieldInfo[] fields = classMeta.GetFields();
			com.db4o.reflect.generic.GenericField[] genericFields = new com.db4o.reflect.generic.GenericField
				[fields.Length];
			for (int i = 0; i < fields.Length; ++i)
			{
				com.db4o.@internal.cs.ClassInfo fieldClassMeta = fields[i].GetFieldClass();
				string fieldName = fields[i].GetFieldName();
				com.db4o.reflect.generic.GenericClass genericFieldClass = ClassMetaToGenericClass
					(reflector, fieldClassMeta);
				genericFields[i] = new com.db4o.reflect.generic.GenericField(fieldName, genericFieldClass
					, fields[i]._isPrimitive, fields[i]._isArray, fields[i]._isNArray);
			}
			genericClass.InitFields(genericFields);
			return genericClass;
		}

		private com.db4o.reflect.generic.GenericClass LookupGenericClass(string className
			)
		{
			return (com.db4o.reflect.generic.GenericClass)_genericClassTable.Get(className);
		}

		private void RegisterGenericClass(string className, com.db4o.reflect.generic.GenericClass
			 classMeta)
		{
			_genericClassTable.Put(className, classMeta);
		}
	}
}
