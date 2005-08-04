using System;
using System.Reflection;
using System.Text;
using j4o.lang;

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

        public GenericType(T1 first, T2 second)
        {
            this.First = first;
            this.Second = second;
        }
    }
#endif

    class TypeNameTest
    {
        class NestedType
        {
        }

        public void testSimpleName()
        {
			try
			{
				TypeName stringName = TypeName.Parse("System.String");
				Tester.ensureEquals("System.String", stringName.SimpleName);
				Tester.ensure(stringName.AssemblyName == null);
				Tester.ensureEquals(0, stringName.GenericArguments.Length);
                Tester.ensureEquals(typeof(string), stringName.Resolve());
			}
			catch (Exception e)
			{
				Tester.error(e);
			}
        }

		public void testVoidPointer()
        {
			TypeName voidPointer = TypeName.Parse("System.Void*");
			Tester.ensureEquals("System.Void", voidPointer.SimpleName);
			Tester.ensure(!voidPointer.HasGenericArguments);
			Tester.ensureEquals(Type.GetType("System.Void*", true), voidPointer.Resolve());
        }

        public void testNestedType()
        {
			try
			{
				TypeName typeName = TypeName.FromType(typeof(NestedType));
				Tester.ensureEquals("com.db4o.test.j4otest.TypeNameTest+NestedType", typeName.SimpleName);
				Tester.ensureEquals(typeof(NestedType), typeName.Resolve());
			}
			catch (Exception e)
			{
				Tester.error(e);
			}

        }

		public void testWrongVersion()
		{
			try
			{
				TypeName stringName = TypeName.Parse("System.String, mscorlib, Version=1.14.27.0");
				Tester.ensureEquals(typeof(string), stringName.Resolve());
			}
			catch (Exception e)
			{
				Tester.error(e);
			}
		}


        public void testAssemblyQualifiedName()
        {
			try
			{
				TypeName stringName = TypeName.FromType(typeof(string));
				Tester.ensureEquals(0, stringName.GenericArguments.Length);
				Tester.ensureEquals("System.String", stringName.SimpleName);
				Tester.ensureEquals(typeof(string).Assembly.FullName, stringName.AssemblyName.FullName);

				Tester.ensureEquals(stringName, TypeName.FromType(typeof(string)));
			}
			catch (Exception e)
			{
				Tester.error(e);
			}

        }

        public void testSimpleArray()
        {
            ensureRoundtrip(typeof(byte[]));
        }

        private static void ensureRoundtrip(Type type)
        {
            try
            {
                TypeName typeName = TypeName.FromType(type);
                Tester.ensureEquals(type, typeName.Resolve());
            }
            catch (Exception e)
            {
                Tester.error(e);
            }
        }

		public void testJaggedArray()
		{
            ensureRoundtrip(typeof(byte[][]));
            
#if !MONO
            ensureRoundtrip(typeof(byte[][][,]));
#endif
		}

#if NET_2_0
        public void testGenericArrays()
        {
            ensureRoundtrip(typeof(SimpleGenericType<string>));
			ensureRoundtrip(typeof(SimpleGenericType<int>[]));
            ensureRoundtrip(typeof(SimpleGenericType<int>[,]));
            ensureRoundtrip(typeof(SimpleGenericType<int>[][]));
            ensureRoundtrip(typeof(SimpleGenericType<int>[][,,]));
        }

        public void testGenericOfArrays()
        {
            ensureRoundtrip(typeof(SimpleGenericType<string[]>));
            ensureRoundtrip(typeof(SimpleGenericType<string[]>[]));
            ensureRoundtrip(typeof(SimpleGenericType<string[,]>[][]));
            ensureRoundtrip(typeof(SimpleGenericType<string[][]>[]));
            ensureRoundtrip(typeof(SimpleGenericType<string[][]>[][]));
            ensureRoundtrip(typeof(SimpleGenericType<SimpleGenericType<string[][]>[][,]>[][]));
        }

        public void testUnversionedGenericName()
        {
			try
			{
				string simpleAssemblyName = Assembly.GetExecutingAssembly().GetName().Name;
				Type t = typeof(GenericType<int, GenericType<int, string>>);
				TypeName tn = TypeName.Parse(t.AssemblyQualifiedName);
				Tester.ensureEquals(
					"com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[System.String, mscorlib]], " + simpleAssemblyName +"]], " + simpleAssemblyName,
					tn.GetUnversionedName());
			}
			catch (Exception e)
			{
				Tester.error(e);
			}
        }

        public void testGenericName()
        {
            GenericType<int, string> o = new GenericType<int, string>(3, "42");
            Type t = Type.GetType(o.GetType().FullName);

            try
            {
                TypeName stringName = TypeName.Parse(typeof(string).AssemblyQualifiedName);

                TypeName genericTypeName = TypeName.Parse(t.AssemblyQualifiedName);
                AssertEquals("com.db4o.test.j4otest.GenericType`2", genericTypeName.SimpleName);
                AssertEquals(2, genericTypeName.GenericArguments.Length);

                AssertEquals(TypeName.Parse(typeof(int).AssemblyQualifiedName), genericTypeName.GenericArguments[0]);
                AssertEquals(stringName, genericTypeName.GenericArguments[1]);

                Type complexType = typeof(GenericType<string, GenericType<int, string>>);
                TypeName complexTypeName = TypeName.Parse(complexType.AssemblyQualifiedName);
                AssertEquals(genericTypeName.SimpleName, complexTypeName.SimpleName);
                AssertEquals(genericTypeName.AssemblyName.FullName, complexTypeName.AssemblyName.FullName);
                AssertEquals(2, complexTypeName.GenericArguments.Length);
                AssertEquals(stringName, complexTypeName.GenericArguments[0]);
                AssertEquals(genericTypeName, complexTypeName.GenericArguments[1]);

                AssertEquals(typeof(string), TypeName.Parse("System.String, mscorlib").Resolve());
                AssertEquals(t, TypeName.Parse("com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[System.String, mscorlib]], " + Assembly.GetExecutingAssembly().GetName().Name).Resolve());
            }
            catch (Exception e)
            {
                Tester.error(e);
            }
        }
#endif

        static void AssertEquals(object expected, object actual)
        {
			Tester.ensureEquals(expected, actual);
        }
    }
}
