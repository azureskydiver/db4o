/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
/*
 * This class is used to mark classes that need to get an autoincremented ID
 */
namespace Db4objects.Db4odoc.Callbacks
{
    abstract class CountedObject
    {
        protected int _id;

        public int Id
        {
            get
            {
                return _id;
            }
            set
            {
                _id = value;
            }
        }
    }
}
