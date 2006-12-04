// Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
using System;
using System.Collections.Generic;
using System.Text;

namespace Db4objects.Db4odoc.Querymode
{
    class Pilot
    {
        private string _name;
        private int _points;  
    
        public Pilot(string name,int points) {
            _name=name;
            _points=points;
        }
            
        public int Points 
        {
            get
            {
                return _points;
            }
            set
            {
                _points = value;
            }
        }
        
        public void AddPoints(int points) 
        {
            _points+=points;
        }
        
        public String Name 
        {
            get 
            {
                return _name;
            }
            set
            {
                _name = value;
            }
        }
        
        public override string ToString() {
            return string.Format("{0}/{1}",_name,_points);
        }
    }
}
