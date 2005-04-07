/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.test.types;
namespace com.db4o.test {

    public class Callbacks 
    {
      
        public static bool returnValue = true;
    
        public static int ACTIVATE = 0;
        public static int DEACTIVATE = 1;
        public static int DELETE = 2;
        public static int NEW = 3;
        public static int UPDATE = 4;
        public static int CAN_ACTIVATE = 5;
        public static int CAN_DEACTIVATE = 6;
        public static int CAN_DELETE = 7;
        public static int CAN_NEW = 8;
        public static int CAN_UPDATE = 9;
    
        public static bool[] called = new bool[CAN_UPDATE + 1];
    
        public String name;
        public CallbackHelper helper;
	
        public void storeOne()
        {
            // helper = new CallbackHelper();
            // helper.parent = this;
            name = "stored";
        }
	
        public void testOne()
        {
	    
            ObjectContainer oc = Test.objectContainer();
	    
            ensure(ACTIVATE);
            ensureNot(DEACTIVATE);
            ensureNot(DELETE);
            ensure(NEW);
            ensureNot(UPDATE);
	    
            ensure(CAN_ACTIVATE);
            ensureNot(CAN_DEACTIVATE);
            ensureNot(CAN_DELETE);
            ensure(CAN_NEW);
            ensureNot(CAN_UPDATE);
            noneCalled();
	    
            returnValue = false;
            oc.deactivate(this,3);
            ensure(CAN_DEACTIVATE);
            ensureNot(DEACTIVATE);
            Test.ensure(name.Equals("stored"));
            noneCalled();
	    
            returnValue = true;
            oc.deactivate(this,3);
            ensure(CAN_DEACTIVATE);
            ensure(DEACTIVATE);
            Test.ensure(name == null);
            noneCalled();
	    
            returnValue = false;
            oc.activate(this,3);
            ensure(CAN_ACTIVATE);
            ensureNot(ACTIVATE);
            Test.ensure(name == null);
            noneCalled();
	    
            returnValue = true;
            oc.activate(this,3);
            ensure(CAN_ACTIVATE);
            ensure(ACTIVATE);
            Test.ensure(name.Equals("stored"));
            noneCalled();
	    
            returnValue = false;
            name = "modified";
            oc.set(this);
            ensure(CAN_UPDATE);
            ensureNot(UPDATE);
            returnValue = true;
            oc.ext().refresh(this, 3);
            Test.ensure(name.Equals("stored"));
            noneCalled();
	    
            returnValue = true;
            name = "modified";
            oc.set(this);
            ensure(CAN_UPDATE);
            ensure(UPDATE);
            oc.ext().refresh(this, 3);
            Test.ensure(name.Equals("modified"));
            noneCalled();
	    
            // Test endless loops
            helper = new CallbackHelper();
            helper.name = "helper";
            helper.parent = this;
            oc.set(this);
            oc.activate(this, 3);
            oc.deactivate(this, 3);
	    
            oc.activate(this, 1);
            oc.deactivate(this.helper, 1);
            returnValue = false;
            noneCalled();
            oc.activate(this, 3);
            ensureNot(ACTIVATE);
	    
            noneCalled();
            returnValue = true;
        }
	
        public bool objectCanActivate(ObjectContainer container)
        {
            called[CAN_ACTIVATE] = true;
            return returnValue;
        }

        public bool objectCanDeactivate(ObjectContainer container)
        {
            called[CAN_DEACTIVATE] = true;
            return returnValue;
        }
	
        public bool objectCanDelete(ObjectContainer container)
        {
            called[CAN_ACTIVATE] = true;
            return returnValue;
        }
	
        public bool objectCanNew(ObjectContainer container)
        {
            called[CAN_NEW] = true;
            return returnValue;
        }
	
        public bool objectCanUpdate(ObjectContainer container)
        {
            called[CAN_UPDATE] = true;
            return returnValue;
        }
	
        public void objectOnActivate(ObjectContainer container)
        {
            called[ACTIVATE] = true;
            if(helper != null)
            {
                container.activate(helper, 3);
            }
        }
	
        public void objectOnDeactivate(ObjectContainer container)
        {
            called[DEACTIVATE] = true;
            if(helper != null)
            {
                container.deactivate(helper, 3);
            }
        }
	
        public void objectOnDelete(ObjectContainer container)
        {
            called[DELETE] = true;
            if(helper != null)
            {
                container.delete(helper);
            }
        }
	
        public void objectOnNew(ObjectContainer container)
        {
            called[NEW] = true;
            if(helper != null)
            {
                container.set(helper);
            }
        }	
	
        public void objectOnUpdate(ObjectContainer container)
        {
            called[UPDATE] = true;
            if(helper != null)
            {
                container.set(helper);
            }
        }
	
        private void ensure(int eventPos)
        {
            Test.ensure(called[eventPos]);
        }
	
        private void ensureNot(int eventPos)
        {
            Test.ensure(! called[eventPos]);
        }

	
        private void noneCalled()
        {
            for (int i = 0; i <= CAN_UPDATE; i++) 
            {
                called[i] = false;
            }
        }
	
    }

}