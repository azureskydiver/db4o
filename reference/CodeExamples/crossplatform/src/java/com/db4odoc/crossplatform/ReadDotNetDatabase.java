package com.db4odoc.crossplatform;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.DotnetSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.WildcardAlias;
import com.db4o.query.Predicate;


public class ReadDotNetDatabase {
    private static final String DATABASE_FILE = "C:\\temp\\database.db4o";

    public static void main(String[] args) {

        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new DotnetSupport(false));

        configuration.common().addAlias(
                new WildcardAlias("Db4odoc.CrossPlatform.CrossPlatform.*, Db4odoc.CrossPlatform",
                        "com.db4odoc.crossplatform.*"));

        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE);
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
    }
}
