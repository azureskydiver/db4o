package com.db4odoc.tutorial.firststeps;


import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class BasicOperations {

    public void openAndCloseDatabase(){
        // #example: Open and close db4o
        ObjectContainer container = Db4oEmbedded.openFile("database.db4o");
        try{
            // do operations on the database
        } finally {
            container.close();
        }
        // #end example
    }
}
