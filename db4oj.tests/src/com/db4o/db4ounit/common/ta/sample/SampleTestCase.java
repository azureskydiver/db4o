/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.sample;

import java.lang.reflect.*;

import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class SampleTestCase extends AbstractDb4oTestCase implements OptOutCS{

    public static void main(String[] args) {
        new SampleTestCase().runAll(); 
    }
    
    protected void configure(Configuration config) throws Exception {
        config.add(new TransparentActivationSupport());
    }
    
    protected void store(){
        Customer customer = new Customer();
        customer._name = "db4objects";
        Address address = new Address();
        customer._addresses = new Address[]{address};
        Country country = new Country();
        address._country = country;
        address._firstLine = "Suite 350";
        State state = new State();
        country._states = new State[]{state};
        state._name = "California";
        City city = new City();
        state._cities = new City[] {city};
        store(customer);
    }
    
    public void testRetrieveNonActivatable() throws Exception{
        Customer customer = (Customer) retrieveOnlyInstance(Customer.class);
        assertIsNotNull(customer, "_name");
        assertIsNotNull(customer, "_addresses");
        Address address = customer._addresses[0];
        assertIsNotNull(address, "_firstLine");
        assertIsNotNull(address, "_country");
        Country country = address._country;
        assertIsNull(country, "_states");
        State state = country.getState("94403");
        assertIsNotNull(country, "_states");
        assertIsNotNull(state, "_name");
        assertIsNotNull(state, "_cities");
        City city = state._cities[0];
        Assert.isNotNull(city);
        assertIsNull(city, "_name");
    }
    
    private void assertIsNull(Object obj, String fieldName) throws Exception{
        Assert.isTrue(fieldIsNull(obj, fieldName));
    }
    
    private void assertIsNotNull(Object obj, String fieldName) throws Exception{
        Assert.isFalse(fieldIsNull(obj, fieldName));
    }
    
    private boolean fieldIsNull(Object obj, String fieldName) throws Exception {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        Assert.isNotNull(field);
        return field.get(obj) == null;
    }

}
