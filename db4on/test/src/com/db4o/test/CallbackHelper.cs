/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.test.types;

namespace com.db4o.test {

    public class CallbackHelper 
    {
      
        public String name;
        public Callbacks parent;
		
        public void objectOnActivate(ObjectContainer container)
        {
		    
            Callbacks.called[Callbacks.ACTIVATE] = true;
		    
		    
            container.activate(parent, 3);
        }
		
        public void objectOnDeactivate(ObjectContainer container)
        {
            container.deactivate(parent, 3);
        }
		
        public void objectOnDelete(ObjectContainer container)
        {
            container.delete(parent);
        }
		
        public void objectOnNew(ObjectContainer container)
        {
            container.set(parent);
        }
		
        public void objectOnUpdate(ObjectContainer container)
        {
		    
            // circular sets are necessary in many cases
            // Don' stop them!
		    
            // Accordingly the following will produce an endless loop
            // container.set(parent);
        }
    }
}