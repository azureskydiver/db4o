/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.listoperations;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;


public class ListOperationsExample {
        public final static String DBFILE = "Test.db";

        public static void main(String[] args)
        {
            fillUpDb(2);
            removeInsert();
            checkResults();
            updateObject();
            checkResults();
        }
        
        private static void fillUpDb(int listCount)
        {
            int dataCount = 50000;
            long elapsedTime = 0;
            new File(DBFILE).delete();
            ObjectContainer db = Db4o.openFile(DBFILE);
            try
            {
            	long t1 = System.currentTimeMillis();

                for (int i = 0; i < listCount; i++)
                {
                    ListObject lo = new ListObject();
                    lo.setName("list" + String.format("%3d", i));
                    for (int j = 0; j < dataCount; j++)
                    {
                        DataObject dataObject = new DataObject();
                        dataObject.setName( "data" + String.format("%5d", j));
                        dataObject.setData( System.currentTimeMillis() + " ---- Data Object " + String.format("%5d", j));
                        lo.getData().add(dataObject);
                    }
                    db.set(lo);
                }
                long t2 = System.currentTimeMillis();
                elapsedTime = t2 - t1;
            }
            finally
            {
                db.close();
            }
            System.out.println("Completed " + listCount + " lists of " + dataCount + " objects each.");
            System.out.println("Elapsed time: " + elapsedTime + " ms.");
        }
        // end fillUpDb

        private static void checkResults()
        {
            ObjectContainer db = Db4o.openFile(DBFILE);
            try
            {
                List<ListObject> result = db.<ListObject>query(ListObject.class);
                if (result.size() > 0)
                {
                	// activation depth should be enough to activate 
                	// ListObject, DataObject and its list members
                    int activationDepth = 3;
                    db.ext().configure().activationDepth(activationDepth);
                    System.out.println("Result count was " + result.size()+ " looping with activation depth" +  activationDepth);
                    for (int i = 0; i < result.size(); i++){
                    	ListObject lo = (ListObject)result.get(i);
                        System.out.println("ListObj " + lo.getName() + " has " + ((lo.getData() == null) ? "<null>" : lo.getData().size()) +" objects");
                        System.out.println((lo.getData() != null && lo.getData().size() > 0) ? lo.getData().get(0).toString() : "<null>" + "  at index 0");
                        System.out.println();
                    }
                }
            }
            finally
            {
                db.close();
            }
        }
        // end checkResults


        private static void removeInsert()
        {
            ObjectContainer db = Db4o.openFile(DBFILE);
            long timeElapsed = 0;
            try
            {
                // set update depth to 1 for the quickest execution
                db.ext().configure().updateDepth(1);
                List<ListObject> result = db.<ListObject>query(ListObject.class);
                if (result.size() == 2)
                {
                	// retrieve 2 ListObjects
                    ListObject lo1 = result.get(0);
                    ListObject lo2 = result.get(1);
                    DataObject dataObject = lo1.getData().get(0);
                    // move the first object from the first
                    // ListObject to the second ListObject
                    lo1.getData().remove(dataObject);
                    lo2.getData().add(dataObject);

                    System.out.println("Removed from the first list, count is " + lo1.getData().size());
                    System.out.println("Added to the second list, count is " + lo2.getData().size());
                    long t1 = System.currentTimeMillis();
                    // save ListObjects. UpdateDepth = 1 will ensure that 
                    // the DataObject list is saved as well
                    db.set(lo1);
                    db.set(lo2);
                    db.commit();
                    long t2 = System.currentTimeMillis();
                    timeElapsed = t2 - t1;
                }
            }
            finally
            {
                db.close();
            }
            System.out.println("Storing took: " + timeElapsed + " ms.");
        }
        // end removeInsert

        private static void updateObject()
        {
            long timeElapsed = 0;

            ObjectContainer db = Db4o.openFile(DBFILE);
            try
            {
                // we can set update depth to 0 
                // as we update only the current object
                db.ext().configure().updateDepth(0);
                List<ListObject> result = db.<ListObject>query(ListObject.class);
                if (result.size() == 2)
                {
                    ListObject lo1 = result.get(0);
                    // Select a DataObject for update
                    DataObject dataobject = lo1.getData().get(0);
                    dataobject.setName("Updated");
                    dataobject.setData(System.currentTimeMillis()+ " ---- Updated Object ");

                    System.out.println("Updated list " + lo1.getName() + " dataobject " +  lo1.getData().get(0));
                    long t1 = System.currentTimeMillis();
                    // save only the DataObject. List of DataObjects will
                    // automatically include the new value
                    db.set(dataobject);
                    db.commit();
                    long t2 = System.currentTimeMillis();
                    timeElapsed = t2 -t1;
                }
            }
            finally
            {
                db.close();
            }
            System.out.println("Storing took: " + timeElapsed +" ms.") ;
        }
        // end updateObject
}
