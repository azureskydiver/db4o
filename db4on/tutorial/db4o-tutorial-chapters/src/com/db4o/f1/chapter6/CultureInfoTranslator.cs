using System.Globalization;
using com.db4o;
using com.db4o.config;

namespace com.db4o.f1.chapter6
{
	public class CultureInfoTranslator : ObjectConstructor
    {
        public object onStore(ObjectContainer container, object applicationObject)
        {
            System.Console.WriteLine("onStore for {0}", applicationObject);
            return ((CultureInfo)applicationObject).Name;
        }
        
        public object onInstantiate(ObjectContainer container, object storedObject)
        {
            System.Console.WriteLine("onInstantiate for {0}", storedObject);
            string name = (string)storedObject;
            return CultureInfo.CreateSpecificCulture(name);
        }
        
        public void onActivate(ObjectContainer container, object applicationObject, object storedObject)
        {
            System.Console.WriteLine("onActivate for {0}/{1}", applicationObject, storedObject);
        }
        
        public j4o.lang.Class storedClass()
        {
            return j4o.lang.Class.getClassForType(typeof(string));
        }
    }
}