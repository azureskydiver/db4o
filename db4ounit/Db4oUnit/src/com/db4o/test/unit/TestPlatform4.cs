using System;
using System.Reflection;
using com.db4o.foundation;

namespace com.db4o.test.unit
{
	public class TestPlatform4
	{
		public static string[] testMethodNames(object typeRef) {
			Type type=normalizeType(typeRef);
			Collection4 names=new Collection4();
			MethodInfo[] methods=type.GetMethods();
			for (int i = 0; i < methods.Length; i++) {
				if(isTestMethod(methods[i])) {
					names.add(methods[i].Name);
				}
			}
			string[] ret=new string[names.size()];
			names.toArray(ret);
			return ret;
		}
		
		private static bool isTestMethod(MethodInfo method) {
			return method.Name.StartsWith("test")
				&& method.IsPublic
				&& method.GetParameters().Length==0;
		}
		
		public static Object create(object typeRef, object[] paramTypes,object[] args) {
			Type type=normalizeType(typeRef);
			Type[] normalizedParams=normalizeTypes(paramTypes);
			try {
				ConstructorInfo constr=type.GetConstructor(normalizedParams);
				return constr.Invoke(args);
			}
			catch(Exception exc) {
				return null;
			}
		}

		public static void runMethod(Object onObject,String methodName,object[] paramTypes,object[] args) {
			Type[] normalizedParams=normalizeTypes(paramTypes);
			try {
				MethodInfo method=onObject.GetType().GetMethod(methodName,normalizedParams);
				method.Invoke(onObject,args);
			}
			catch(TargetInvocationException exc) {
				throw exc.InnerException;
			}
		}
	
		private static Type[] normalizeTypes(object[] typeRefs) {
			if(typeRefs is Type[]) {
				return (Type[])typeRefs;
			}
			Type[] normalized=new Type[typeRefs.Length];
			for(int idx=0;idx<typeRefs.Length;idx++) {
				normalized[idx]=normalizeType(typeRefs[idx]);
			}
			return normalized;
		}
	
		private static Type normalizeType(object typeRef) {
			if(typeRef is j4o.lang.Class) {
				typeRef=((j4o.lang.Class)typeRef).getNetType();
			}
			return (Type)typeRef;
		}
	}
}