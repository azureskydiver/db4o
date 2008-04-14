/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.listoperations;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;

public class ListOperationsExample {
        private final static String DB4O_FILE_NAME = "reference.db4o";

        public static void main(String[] args)
        {
            fillUpDb(2);
            removeInsert();
            checkResults();
            //updateObject();
            //checkResults();
        }
        
        private static void fillUpDb(int listCount)
        {
            int dataCount = 5;
            long elapsedTime = 0;
            new File(DB4O_FILE_NAME).delete();
            ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
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
                    container.store(lo);
                }
                long t2 = System.currentTimeMillis();
                elapsedTime = t2 - t1;
            }
            finally
            {
                container.close();
            }
            System.out.println("Completed " + listCount + " lists of " + dataCount + " objects each.");
            System.out.println("Elapsed time: " + elapsedTime + " ms.");
        }
        // end fillUpDb

        private static void checkResults()
        {
        	// activation depth should be enough to activate 
        	// ListObject, DataObject and its list members
            int activationDepth = 3;
            Configuration configuration = Db4o.newConfiguration();
            configuration.activationDepth(activationDepth);
            ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
            try
            {
                List<ListObject> result = container.<ListObject>query(ListObject.class);
                if (result.size() > 0)
                {
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
                container.close();
            }
        }
        // end checkResults


        private static void removeInsert()
        {
        	// set update depth to 1 for the quickest execution
        	Configuration configuration = Db4o.newConfiguration();
        	configuration.updateDepth(1);
            ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
            long timeElapsed = 0;
            try
            {
                List<ListObject> result = container.<ListObject>query(ListObject.class);
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
                    container.store(lo1);
                    container.store(lo2);
                    container.commit();
                    long t2 = System.currentTimeMillis();
                    timeElapsed = t2 - t1;
                }
            }
            finally
            {
                container.close();
            }
            System.out.println("Storing took: " + timeElapsed + " ms.");
        }
        // end removeInsert

        private static void updateObject()
        {
            long timeElapsed = 0;
            // we can set update depth to 0 
            // as we update only the current object
            Configuration configuration = Db4o.newConfiguration();
            configuration.updateDepth(0);
            ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
            try
            {
                List<ListObject> result = container.<ListObject>query(ListObject.class);
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
                    container.store(dataobject);
                    container.commit();
                    long t2 = System.currentTimeMillis();
                    timeElapsed = t2 -t1;
                }
            }
            finally
            {
                container.close();
            }
            System.out.println("Storing took: " + timeElapsed +" ms.") ;
        }
        // end updateObject
}
