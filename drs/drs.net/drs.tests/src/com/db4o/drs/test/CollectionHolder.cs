using System.Collections;

namespace com.db4o.drs.test
{
    public class CollectionHolder
    {
        public string name;

        public System.Collections.IDictionary ht = new System.Collections.Hashtable();

        public ArrayList list = new ArrayList();

        public IDictionary set = new Hashtable();

        public CollectionHolder()
        {
        }

        public CollectionHolder(string name)
        {
            this.name = name;
        }

        public override string ToString()
        {
            return name + ", hashcode = " + GetHashCode();
        }
    }
}
