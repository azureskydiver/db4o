package demo.objectmanager.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * User: treeder
 * Date: Aug 9, 2006
 * Time: 1:18:41 PM
 */
public class Contact {
    private Integer id;
    private String name;
    private int age;
    private List addresses;
    private List emailAddresses;
    private Note note;
    private List friends;
	private Date created;
	private double income;
	private Date birthDate;
	private char gender;

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

    public void setEmailAddresses(List emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public List getFriends() {
        return friends;
    }

    public void setFriends(List friends) {
        this.friends = friends;
    }


    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String toString() {
        return "Contact: id=" + id + " name=" + name + " age=" + age;
    }

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}
}