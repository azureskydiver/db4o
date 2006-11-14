using System.Collections;

namespace Db4objects.Drs.Test
{
    public class CollectionHandlerImplTest : Db4objects.Drs.Test.DrsTestCase
    {
        private Db4objects.Db4o.Reflect.IReflector _reflector = Db4objects.Drs.Inside.ReplicationReflector
            .GetInstance().Reflector();

        private Db4objects.Drs.Inside.CollectionHandlerImpl _collectionHandler = new Db4objects.Drs.Inside.CollectionHandlerImpl
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
