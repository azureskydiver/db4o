package demo.objectmanager.model;


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


    public Address() {
    }

    public Address(Contact c, String street, String city, String state, String zip) {
        contact = c;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}

