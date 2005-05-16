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
            TypeName stringName = TypeName.Parse("System.String");
            Test.ensureEquals("System.String", stringName.SimpleName);
            Test.ensure(stringName.AssemblyName == null);
            Test.ensureEquals(0, stringName.GenericArguments.Length);

            Test.ensureEquals(typeof(string), stringName.Resolve());
        }

        public void testNestedType()
        {
            TypeName typeName = TypeName.Parse(typeof(NestedType).AssemblyQualifiedName);
            Test.ensureEquals("com.db4o.test.j4otest.TypeNameTest+NestedType", typeName.SimpleName);
            Test.ensureEquals(typeof(NestedType), typeName.Resolve());
        }

        public void testWrongVersion()
        {
            TypeName stringName = TypeName.Parse("System.String, mscorlib, Version=1.14.27.0");
            Test.ensureEquals(typeof(string), stringName.Resolve());
        }

        public void testAssemblyQualifiedName()
        {
            TypeName stringName = TypeName.Parse(typeof(string).AssemblyQualifiedName);
            Test.ensureEquals(0, stringName.GenericArguments.Length);
            Test.ensureEquals("System.String", stringName.SimpleName);
            Test.ensureEquals(typeof(string).Assembly.FullName, stringName.AssemblyName.FullName);

            Test.ensureEquals(stringName, TypeName.Parse(typeof(string).AssemblyQualifiedName));
        }

        public void testSimpleArray()
        {
            TypeName arrayTypeName = TypeName.Parse(typeof(byte[]).AssemblyQualifiedName);
            Test.ensureEquals(typeof(byte[]), arrayTypeName.Resolve());
        }

#if NET_2_0
        public void testGenericArrays()
        {
            TypeName simpleGType = TypeName.Parse(typeof(SimpleGenericType<string>).AssemblyQualifiedName);
            Test.ensureEquals(typeof(SimpleGenericType<string>), simpleGType.Resolve());

            TypeName genericArrayType = TypeName.Parse(typeof(SimpleGenericType<int>[]).AssemblyQualifiedName);
            Test.ensureEquals(typeof(SimpleGenericType<int>[]), genericArrayType.Resolve());

            genericArrayType = TypeName.Parse(typeof(SimpleGenericType<int>[,]).AssemblyQualifiedName);
            AssertEquals(typeof(SimpleGenericType<int>[,]), genericArrayType.Resolve());
        }

        public void testUnversionedGenericName()
        {
            string simpleAssemblyName = Assembly.GetExecutingAssembly().GetName().Name;
            Type t = typeof(GenericType<int, GenericType<int, string>>);
            TypeName tn = TypeName.Parse(t.AssemblyQualifiedName);
            Test.ensureEquals(
                "com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[com.db4o.test.j4otest.GenericType`2[[System.Int32, mscorlib],[System.String, mscorlib]], " + simpleAssemblyName +"]], " + simpleAssemblyName,
                tn.GetUnversionedName());
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
                Test.error(e);
            }
        }
#endif

        static void AssertEquals(object expected, object actual)
        {
            if (!object.Equals(expected, actual))
            {
                throw new ApplicationException(string.Format("'{0}' != '{1}'", expected, actual));
            }
        }
    }
}
