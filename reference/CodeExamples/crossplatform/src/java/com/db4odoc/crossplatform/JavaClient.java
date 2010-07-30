package com.db4odoc.crossplatform;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.DotnetSupport;
import com.db4o.config.WildcardAlias;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ClientConfiguration;
import com.db4o.query.Predicate;

/**
 * A Java client which can connect to a .NET db4o server
 */
public class JavaClient {
    public static void main(String[] args) throws Exception {
        ClientConfiguration configuration = Db4oClientServer.newClientConfiguration();

        // add the support for .NET
        configuration.common().add(new DotnetSupport(true));

        // #example: You need to add aliases for your types
        configuration.common().addAlias(
                new WildcardAlias("Db4odoc.CrossPlatform.CrossPlatform.*, Db4odoc.CrossPlatform",
                                    "com.db4odoc.crossplatform.*"));
        // #end example

        ObjectContainer container = Db4oClientServer.openClient(configuration,
                "localhost", 1337, "sa", "sa");


        try {
            container.store(new Person("Joe", "Average"));
            container.store(new Person("Joe", "Johnson"));
            container.store(new Person("Noel", "Exceptional"));

            ObjectSet<Person> persons = container.query(new Predicate<Person>() {
                @Override
                public boolean match(Person person) {
                    return person.getFirstname().equals("Joe");
                }
            });
            for (Person person : persons) {
                System.out.println(person);
            }
        } finally {
            container.close();
        }
        System.out.println("Done.");
    }
}
