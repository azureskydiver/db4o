package com.db4odoc.container.sessions;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;


public class Db4oSessions {
    private static final String DATABASE_FILE_NAME = "database.db4o";


    public void sessions(){
        // #example: Session object container
        EmbeddedObjectContainer rootContainer = Db4oEmbedded.openFile(DATABASE_FILE_NAME);

        // open the db4o-session. For example at the beginning for a web-request
        ObjectContainer session = rootContainer.openSession();
        try{
            // do the operations on the session-container
            session.store(new Person("Joe"));
        } finally {
            // close the container. For example when the request ends
            session.close();
        }
        // #end example

        rootContainer.close();
    }

    public void embeddedClient(){
        // #example: Embedded client
        ObjectServer server = Db4oClientServer.openServer(DATABASE_FILE_NAME,0);

        // open the db4o-embedded client. For example at the beginning for a web-request
        ObjectContainer container = server.openClient();
        try{
            // do the operations on the session-container
            container.store(new Person("Joe"));
        } finally {
            // close the container. For example when the request ends
            container.close();
        }
        // #end example

        server.close();
    }


    private static class Person{
        private String name;

        private Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
