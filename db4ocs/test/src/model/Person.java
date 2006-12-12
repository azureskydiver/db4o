package model;

import java.io.Serializable;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 10:40:09 PM
 */
public class Person implements Serializable {
	private int id;
	private String name;
	private Person friend;
	private Car car;

	public Person() {
	}

	public Person(String name) {
		this.name = name;
	}

	public Person getFriend() {
		return friend;
	}

	public void setFriend(Person friend) {
		this.friend = friend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return "[" + id + "] " + name + " - " + super.toString();
	}

	public void setCar(Car car) {
		this.car = car;
	}
}
