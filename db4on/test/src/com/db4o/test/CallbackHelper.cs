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
		
        public void ObjectOnActivate(ObjectContainer container)
        {
		    
            Callbacks.called[Callbacks.ACTIVATE] = true;
		    
		    
            container.Activate(parent, 3);
        }
		
        public void ObjectOnDeactivate(ObjectContainer container)
        {
            container.Deactivate(parent, 3);
        }
		
        public void ObjectOnDelete(ObjectContainer container)
        {
            container.Delete(parent);
        }
		
        public void ObjectOnNew(ObjectContainer container)
        {
            container.Set(parent);
        }
		
        public void ObjectOnUpdate(ObjectContainer container)
        {
		    
            // circular sets are necessary in many cases
            // Don' stop them!
		    
            // Accordingly the following will produce an endless loop
            // container.Set(parent);
        }
    }
}