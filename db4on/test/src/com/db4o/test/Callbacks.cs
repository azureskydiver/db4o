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
	
        public void StoreOne()
        {
            // helper = new CallbackHelper();
            // helper.parent = this;
            name = "stored";
        }
	
        public void TestOne()
        {
	    
            ObjectContainer oc = Tester.ObjectContainer();
	    
            Ensure(ACTIVATE);
            EnsureNot(DEACTIVATE);
            EnsureNot(DELETE);
            Ensure(NEW);
            EnsureNot(UPDATE);
	    
            Ensure(CAN_ACTIVATE);
            EnsureNot(CAN_DEACTIVATE);
            EnsureNot(CAN_DELETE);
            Ensure(CAN_NEW);
            EnsureNot(CAN_UPDATE);
            NoneCalled();
	    
            returnValue = false;
            oc.Deactivate(this,3);
            Ensure(CAN_DEACTIVATE);
            EnsureNot(DEACTIVATE);
            Tester.Ensure(name.Equals("stored"));
            NoneCalled();
	    
            returnValue = true;
            oc.Deactivate(this,3);
            Ensure(CAN_DEACTIVATE);
            Ensure(DEACTIVATE);
            Tester.Ensure(name == null);
            NoneCalled();
	    
            returnValue = false;
            oc.Activate(this,3);
            Ensure(CAN_ACTIVATE);
            EnsureNot(ACTIVATE);
            Tester.Ensure(name == null);
            NoneCalled();
	    
            returnValue = true;
            oc.Activate(this,3);
            Ensure(CAN_ACTIVATE);
            Ensure(ACTIVATE);
            Tester.Ensure(name.Equals("stored"));
            NoneCalled();
	    
            returnValue = false;
            name = "modified";
            oc.Set(this);
            Ensure(CAN_UPDATE);
            EnsureNot(UPDATE);
            returnValue = true;
            oc.Ext().Refresh(this, 3);
            Tester.Ensure(name.Equals("stored"));
            NoneCalled();
	    
            returnValue = true;
            name = "modified";
            oc.Set(this);
            Ensure(CAN_UPDATE);
            Ensure(UPDATE);
            oc.Ext().Refresh(this, 3);
            Tester.Ensure(name.Equals("modified"));
            NoneCalled();
	    
            // Tester endless loops
            helper = new CallbackHelper();
            helper.name = "helper";
            helper.parent = this;
            oc.Set(this);
            oc.Activate(this, 3);
            oc.Deactivate(this, 3);
	    
            oc.Activate(this, 1);
            oc.Deactivate(this.helper, 1);
            returnValue = false;
            NoneCalled();
            oc.Activate(this, 3);
            EnsureNot(ACTIVATE);
	    
            NoneCalled();
            returnValue = true;
        }
	
        public bool ObjectCanActivate(ObjectContainer container)
        {
            called[CAN_ACTIVATE] = true;
            return returnValue;
        }

        public bool ObjectCanDeactivate(ObjectContainer container)
        {
            called[CAN_DEACTIVATE] = true;
            return returnValue;
        }
	
        public bool ObjectCanDelete(ObjectContainer container)
        {
            called[CAN_ACTIVATE] = true;
            return returnValue;
        }
	
        public bool ObjectCanNew(ObjectContainer container)
        {
            called[CAN_NEW] = true;
            return returnValue;
        }
	
        public bool ObjectCanUpdate(ObjectContainer container)
        {
            called[CAN_UPDATE] = true;
            return returnValue;
        }
	
        public void ObjectOnActivate(ObjectContainer container)
        {
            called[ACTIVATE] = true;
            if(helper != null)
            {
                container.Activate(helper, 3);
            }
        }
	
        public void ObjectOnDeactivate(ObjectContainer container)
        {
            called[DEACTIVATE] = true;
            if(helper != null)
            {
                container.Deactivate(helper, 3);
            }
        }
	
        public void ObjectOnDelete(ObjectContainer container)
        {
            called[DELETE] = true;
            if(helper != null)
            {
                container.Delete(helper);
            }
        }
	
        public void ObjectOnNew(ObjectContainer container)
        {
            called[NEW] = true;
            if(helper != null)
            {
                container.Set(helper);
            }
        }	
	
        public void ObjectOnUpdate(ObjectContainer container)
        {
            called[UPDATE] = true;
            if(helper != null)
            {
                container.Set(helper);
            }
        }
	
        private void Ensure(int eventPos)
        {
            Tester.Ensure(called[eventPos]);
        }
	
        private void EnsureNot(int eventPos)
        {
            Tester.Ensure(! called[eventPos]);
        }

	
        private void NoneCalled()
        {
            for (int i = 0; i <= CAN_UPDATE; i++) 
            {
                called[i] = false;
            }
        }
	
    }

}