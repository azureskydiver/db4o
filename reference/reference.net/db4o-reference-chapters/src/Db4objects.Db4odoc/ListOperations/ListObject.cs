/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System.Collections.Generic;

namespace Db4objects.Db4odoc.ListOperations
{
    class ListObject
    {
        string _name;
        List<DataObject> _data;

        public ListObject() 
        {
            _data = new List<DataObject>(); 
        }
        
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        public List<DataObject> Data
        {
            get { return _data; }
            set { _data = value; }
        }
    }
}
