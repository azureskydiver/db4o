/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.ListDeleting
{
    class DataObject
    {
        string _name;
        string _data;

        public DataObject() 
        { }

        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        public string Data
        {
            get { return _data; }
            set { _data = value; }
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}", _name, _data);
        }
    }
}
