/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
/*
 * Singleton class used to keep auotincrement information 
 * and give the next available ID on request
 */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Callbacks
{
    class IncrementedId
    {
        private int _no;
        private static IncrementedId _ref;

        private IncrementedId()
        {
            _no = 0;
        }
        // end IncrementedId

        public int GetNextID(IObjectContainer db)
        {
            _no++;
            db.Set(this);
            return _no;
        }
        // end increment

        public static IncrementedId GetIdObject(IObjectContainer db)
        {
            // if _ref is not assigned yet:
            if (_ref == null)
            {
                // check if there is a stored instance from the previous 
                // session in the database
                IObjectSet os = db.Get(typeof(IncrementedId));
                if (os.Size() > 0)
                    _ref = (IncrementedId)os.Next();
            }

            if (_ref == null)
            {
                // create new instance and store it
                Console.WriteLine("Id object is created");
                _ref = new IncrementedId();
                db.Set(_ref);
            }
            return _ref;
        }
        // end getIdObject
    }
}

