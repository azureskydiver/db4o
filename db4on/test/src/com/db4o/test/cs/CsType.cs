using System;

namespace com.db4o.test.cs
{

	public class CsType
	{

        Type myType;

        public void configure(){
            // apparently the translator is not found
            // please check
            Db4o.configure().objectClass(typeof(Type)).translate(new TSerializable());
        }

        public void storeOne(){
            myType = typeof(String);
        }

        public void testOne(){
            Test.ensure(myType == typeof(String));
        }


	}
}
