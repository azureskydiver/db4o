using System.Collections;

namespace com.db4o.drs.test
{
    public class CollectionHandlerImplTest : com.db4o.drs.test.DrsTestCase
    {
        private com.db4o.reflect.Reflector _reflector = com.db4o.drs.inside.ReplicationReflector
            .GetInstance().Reflector();

        private com.db4o.drs.inside.CollectionHandlerImpl _collectionHandler = new com.db4o.drs.inside.CollectionHandlerImpl
            ();

        public CollectionHandlerImplTest()
        {
        }

        public virtual void Test()
        {
            TstVector();
            //			TstList();
            TstSet();
            TstMap();
            TstString();
            _reflector = null;
            _collectionHandler = null;
        }

        public virtual void TstVector()
        {
            System.Collections.ArrayList vector = new System.Collections.ArrayList();
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(vector));
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(_reflector.ForObject(vector))
                );
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(typeof(System.Collections.ArrayList
                )));
        }

        //		public virtual void TstList()
        //		{
        //			System.Collections.IList list = new j4o.util.LinkedList();
        //			Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(list));
        //			Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(_reflector.ForObject(list)));
        //			Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(typeof(System.Collections.IList
        //				)));
        //		}

        public virtual void TstSet()
        {
            IDictionary set = new Hashtable();
            //j4o.util.Set set = new j4o.util.HashSet();
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(set));
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(_reflector.ForObject(set)));
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(typeof(Hashtable)));
        }

        public virtual void TstMap()
        {
            System.Collections.IDictionary map = new System.Collections.Hashtable();
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(map));
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(_reflector.ForObject(map)));
            Db4oUnit.Assert.IsTrue(_collectionHandler.CanHandle(typeof(System.Collections.IDictionary
                )));
        }

        public virtual void TstString()
        {
            string str = "abc";
            Db4oUnit.Assert.IsTrue(!_collectionHandler.CanHandle(str));
            Db4oUnit.Assert.IsTrue(!_collectionHandler.CanHandle(_reflector.ForObject(str)));
            Db4oUnit.Assert.IsTrue(!_collectionHandler.CanHandle(typeof(string)));
        }
    }
}
