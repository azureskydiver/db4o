/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.listdeleting;


public class DataObject {
        String _name;
        String _data;

        public DataObject() 
        { }

        public String getName()
        {
            return _name; 
        }
        
        public void setName(String name)
        {
            _name = name; 
        }

        public String getData()
        {
            return _data;
        }
        
        public void setData(String data){
            _data = data;
        }

        public String toString()
        {
            return String.format("%s/%s}", _name, _data);
        }
    }

