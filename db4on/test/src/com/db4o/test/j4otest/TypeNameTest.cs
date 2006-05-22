using System;
using System.Globalization;
using System.Reflection;
using System.Text;
using j4o.lang;
#if NET_2_0
using System.Collections.Generic;
#endif

namespace com.db4o.test.j4otest
{
#if NET_2_0
    class SimpleGenericType<T>
    {
        public T Value;
    }    

    class GenericType<T1, T2>
    {
        public T1 First;
        public T2 Second;

        public class NestedInGeneric
        {
        }

        public GenericType(T1 first, T2 second)
        {
            this.First = first;
            this.Second = second;
        }
    }
#endif

    class TypeNameTest
    {
		class __Funny123Name_
		{
		}
    	
        class NestedType
        {
        }
    	
    	public void TestFunnyName()
    	{
    		EnsureRoundtrip(typeof(__Funny123Name_));
    	}
    	
        public void TestSimpleName()
        {
			try
			{
				TypeReference stringName = TypeReference.FromString("System.String");
				Tester.EnsureEquals("System.String", stringName.SimpleName);
				Tester.Ensure(stringName.AssemblyName == null);
                Tester.EnsureEquals(typeof(string), stringName.Resolve());
			}
			catch (Exception e)
			{
				Tester.Error(e);
			}
        }

		public void TestVoidPointer()
        {
			TypeReference voidPointer = TypeReference.FromString("System.Void*");
			Tester.EnsureEquals("System.Void", voidPointer.SimpleName);
			Tester.Ensure(voidPointer is PointerTypeReference);
			Tester.EnsureEquals(Type.GetType("System.Void*", true), voidPointer.Resolve());
        }

        public void TestNestedType()
        {
			try
			{
				TypeReference typeName = TypeReference.FromType(typeof(NestedType));
				Tester.EnsureEquals("com.db4o.test.j4otest.TypeNameTest+NestedType", typeName.SimpleName);
				Tester.EnsureEquals(typeof(NestedType), typeName.Resolve());
			}
			catch (Exception e)
			{
				Tester.Error(e);
			}

        }

		public void TestWrongVersion()
		{
			try
			{
				TypeReference stringName = TypeReference.FromString("System.String, mscorlib, Version=1.14.27.0");
				Tester.EnsureEquals(typeof(string), stringName.Resolve());
			}
			catch (Exception e)
			{
				Tester.Error(e);
			}
		}

		public void TestAssemblyNameWithSpaces()
		{
			TypeReference typeReference =
				TypeReference.FromString("Foo, Business Objects, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null");
			Tester.EnsureEquals("Foo", typeReference.SimpleName);
			Tester.EnsureEquals("Business Objects", typeReference.AssemblyName.Name);
		}

        public void TestAssemblyQualifiedName()
        {
			try
			{
				string assemblyNameString = "mscorlib, Version=2.0.0.0, Culture=neutral, PublicKeyToken=969db8053d3322ac";
				TypeReference typeReference =
					TypeReference.FromString(
						"System.String, " + assemblyNameString);
				Tester.EnsureEquals("System.String", typeReference.SimpleName);
				
				AssemblyName assemblyName = new AssemblyName();
				assemblyName.Name = "mscorlib";
				assemblyName.Version = new Version(2, 0, 0, 0);
				assemblyName.CultureInfo = CultureInfo.InvariantCulture;
				assemblyName.SetPublicKeyToken(ParsePublicKeyToken("969db8053d3322ac"));
				Tester.EnsureEquals(assemblyName.FullName, typeReference.AssemblyName.FullName, "string.Assembly.FullName");
			}
			catch (Exception e)
			{
				Tester.Error(e);
			}

        }
    	
		static byte[] ParsePublicKeyToken(string token)
		{
			int len = token.Length / 2;
			byte[] bytes = new byte[len];
			for (int i = 0; i < len; ++i)
			{
				bytes[i] = byte.Parse(token.Substring(i * 2, 2), System.Globalization.NumberStyles.HexNumber);
			}
			return bytes;
		}

        public void TestSimpleArray()
        {
            EnsureRoundtrip(typeof(byte[]));
        }

        private static void EnsureRoundtrip(Type type)
        {
            try
            {
                TypeReference typeName = TypeReference.FromType(type);
				Tester.EnsureEquals(type, typeName.Resolve(), type.FullName);
            }
            catch (Exception e)
            {
                Tester.Error(e);
            }
        }

		public void TestJaggedArray()
		{
            EnsureRoundtrip(typeof(byte[][]));
            
#if !MONO
            EnsureRoundtrip(typeof(byte[][][,]));
#endif
		}

#if NET_2_0
        class NestedGeneric<Key, Value>
        {
        }

        public void TestDeepGenericTypeName()
        {
            EnsureRoundtrip(typeof(Dictionary<string, List<string>>));
            EnsureRoundtrip(typeof(Dictionary<string, List<List<string>>>));

            EnsureRoundtrip(typeof(Dictionary<string, List<List<NestedType>>>));
            EnsureRoundtrip(typeof(NestedGeneric<string, List<string>[]>));
            EnsureRoundtrip(typeof(NestedGeneric<string, List<string>>[]));

            EnsureRoundtrip(typeof(GenericType<string, List<string>>.NestedInGeneric));
        }

        public void TestGenericArrays()
        {
            EnsureRoundtrip(typeof(SimpleGenericType<string>));
			EnsureRoundtrip(typeof(SimpleGenericType<int>[]));
            EnsureRoundtrip(typeof(SimpleGenericType<int>[,]));
            EnsureRoundtrip(typeof(SimpleGenericType<int>[][]));
            EnsureRoundtrip(typeof(SimpleGenericType<int>[][,,]));
        }

        public void TestGenericOfArrays()
        {
            EnsureRoundtrip(typeof(SimpleGenericType<string[]>));
            EnsureRoundtrip(typeof(SimpleGenericType<string[]>[]));
            EnsureRoundtrip(typeof(SimpleGenericType<string[,]>[][]));
            EnsureRoundtrip(typeof(SimpleGenericType<string[][]>[]));
            EnsureRoundtrip(typeof(SimpleGenericType<string[][]>[][]));
            EnsureRoundtrip(typeof(SimpleGenericType<SimpleGenericType<string[][]>[][,]>[][]));
        }

        public void TestUnversionedGenericName()
        {
			try
			{
				string simpleAssemblyName = GetExecutingAssemblySimpleName();
				Type t = typeof(GenericType<int, GenericType<int, string>>);
				TypeReference tn = TypeReference.FromString(t.AssemblyQualifiedName);
				Tester.EnsureEquals(
					"com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib], [com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib], [System.String, mscorlib]], " + simpleAssemblyName +"]], " + simpleAssemblyName,
					tn.GetUnversionedName());
			}
			catch (Exception e)
			{
				Tester.Error(e);
			}
        }

        public void TestGenericName()
        {
            GenericType<int, string> o = new GenericType<int, string>(3, "42");
            Type t = Type.GetType(o.GetType().FullName);

            try
            {
                TypeReference stringName = TypeReference.FromString(typeof(string).AssemblyQualifiedName);

                GenericTypeReference genericTypeName = (GenericTypeReference)TypeReference.FromString(t.AssemblyQualifiedName);
                AssertEquals("com.db4o.test.j4otest.GenericType`2", genericTypeName.SimpleName);
                AssertEquals(2, genericTypeName.GenericArguments.Length);

                AssertEquals(TypeReference.FromString(typeof(int).AssemblyQualifiedName), genericTypeName.GenericArguments[0]);
                AssertEquals(stringName, genericTypeName.GenericArguments[1]);

                Type complexType = typeof(GenericType<string, GenericType<int, string>>);
                GenericTypeReference complexTypeName = (GenericTypeReference) TypeReference.FromString(complexType.AssemblyQualifiedName);
                AssertEquals(genericTypeName.SimpleName, complexTypeName.SimpleName);
                AssertEquals(genericTypeName.AssemblyName.FullName, complexTypeName.AssemblyName.FullName);
                AssertEquals(2, complexTypeName.GenericArguments.Length);
                AssertEquals(stringName, complexTypeName.GenericArguments[0]);
                AssertEquals(genericTypeName, complexTypeName.GenericArguments[1]);

                AssertEquals(typeof(string), TypeReference.FromString("System.String, mscorlib").Resolve());
                AssertEquals(t, TypeReference.FromString("com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[System.String, mscorlib]], " + GetExecutingAssemblySimpleName()).Resolve());
            }
            catch (Exception e)
            {
                Tester.Error(e);
            }
        }

    	private static string GetExecutingAssemblySimpleName()
    	{
    		return Assembly.GetExecutingAssembly().GetName().Name;
    	}
#endif

        static void AssertEquals(object expected, object actual)
        {
			Tester.EnsureEquals(expected, actual);
        }
    }
}
