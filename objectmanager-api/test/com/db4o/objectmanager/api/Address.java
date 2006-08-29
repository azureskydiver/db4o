package com.db4o.objectmanager.api;

/**
 * User: treeder
 * Date: Aug 29, 2006
 * Time: 9:52:35 AM
 */
public class Address {
    Contact contact; // bi-directional
    String street;
    String city;
    String state;
    String zip;
}
