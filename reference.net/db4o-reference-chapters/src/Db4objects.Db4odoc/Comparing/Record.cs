/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Comparing
{
    class Record
    {
        private MyString _record;


        public Record(string record)
        {
            _record = new MyString(record);
        }

        public override string ToString()
        {
            return _record.ToString();
        }
    }
}
