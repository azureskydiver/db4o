// #example: The person class in Java
package com.db4odoc.crossplatform;

public class Person {
    private String firstname;
    private String sirname;

    public Person(String firstname, String sirname) {
        this.firstname = firstname;
        this.sirname = sirname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSirname() {
        return sirname;
    }

    public void setSirname(String sirname) {
        this.sirname = sirname;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstname='" + firstname + '\'' +
                ", sirname='" + sirname + '\'' +
                '}';
    }
}
// #end example
