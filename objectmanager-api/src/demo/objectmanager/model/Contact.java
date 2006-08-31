package demo.objectmanager.model;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Aug 9, 2006
 * Time: 1:18:41 PM
 */
public class Contact {
    private Integer id;
    private String name;
    private int age;
    List addresses;
    List emails;
    private List emailAddresses;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List getAddresses() {
        if(addresses == null) addresses = new ArrayList();
        return addresses;
    }

    public void setAddresses(List addresses) {
        this.addresses = addresses;
    }

    public List getEmails() {
        return emails;
    }

    public void setEmails(List emails) {
        this.emails = emails;
    }

    public void addAddress(Address address) {
        getAddresses().add(address);
    }

    public void addEmail(EmailAddress emailAddress) {
        getEmailAddresses().add(emailAddress);
    }

    public List getEmailAddresses() {
        if(emailAddresses == null) emailAddresses = new ArrayList();
        return emailAddresses;
    }
}