package com.db4odoc.practises.relations;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;


public class RelationManagementExamples {

    public static void main(String[] args) {
        ObjectContainer container = Db4oEmbedded.openFile("database.db4o");
        try {
           storeTestData(container);

           loadPersonsOfACountry(container);
        } finally {
            container.close();
        }
    }

    private static void storeTestData(ObjectContainer container) {
        Country switzerland = new Country("Switzerland");
        Country china = new Country("China");
        Country japan = new Country("Japan");
        Country usa = new Country("USA");
        Country germany = new Country("Germany");

        container.store(new Person("Berni","Gian-Reto",switzerland));
        container.store(new Person("Wang","Long",china));
        container.store(new Person("Tekashi","Amuro",japan));
        container.store(new Person("Miller","John",usa));
        container.store(new Person("Smith","Paul",usa));
        container.store(new Person("Müller","Hans",germany));

    }

    private static void loadPersonsOfACountry(ObjectContainer container) {
        // #example: Query for people burn in a country
        final Country country = loadCountry(container,"USA");
        final ObjectSet<Person> peopleBurnInTheUs = container.query(new Predicate<Person>() {
            @Override
            public boolean match(Person p) {
                return p.getBornIn() == country;
            }
        });
        // #end example
        System.out.println(peopleBurnInTheUs.size());

    }

    private static Country loadCountry(ObjectContainer container,final String countryName) {
        return container.query(new Predicate<Country>() {
            @Override
            public boolean match(Country c) {
                return c.getName().equals(countryName);
            }
        }).get(0);
    }
}
