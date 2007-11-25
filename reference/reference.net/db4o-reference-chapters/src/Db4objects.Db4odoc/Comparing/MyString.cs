/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Comparing
{
    class MyString
    {
        private string _string;
    	
    	public MyString(string str){
    		_string = str;
    	}
    	
    	public override string ToString(){
    		return _string;
    	}
    }
}
