using System;


namespace com.db4o.reflect.net
{
	/// <summary>Constructs objects by using System.Runtime.Serialization.FormatterServices.GetUninitializedObject
	/// and bypasses calls to user contructors this way. Not available on CompactFramework
	/// </summary>
	public class SerializationConstructor : com.db4o.reflect.ReflectConstructor
	{
        private Type _type;

		public SerializationConstructor(Type type){
            _type = type;
		}

        public virtual com.db4o.reflect.ReflectClass[] getParameterTypes() {
            return null;
        }

        public virtual void setAccessible() {
            // do nothing
        }

        public virtual object newInstance(object[] parameters) {
            return System.Runtime.Serialization.FormatterServices.GetUninitializedObject(_type);
        }
	}
}
