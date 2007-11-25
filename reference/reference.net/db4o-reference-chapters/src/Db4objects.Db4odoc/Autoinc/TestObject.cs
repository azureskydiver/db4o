/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Autoinc
{
    class TestObject: CountedObject
    {
        string _name;	
	
	    public TestObject(string name) {
		    _name = name;
	    }
    	
	    public override string ToString() {
		    return _name+"/"+_id;
	    }
    }
}
