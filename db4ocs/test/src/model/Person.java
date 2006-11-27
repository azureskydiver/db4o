package model;

import java.io.Serializable;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 10:40:09 PM
 */
public class Person implements Serializable {
	private int index;
	private String name;
	private Person friend;
	private Car car;

	public Person(String name) {

		this.name = name;
	}

	public Person() {

	}


	public Person getFriend() {
		return friend;
	}

	public void setFriend(Person friend) {
		this.friend = friend;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return index + " " + name + " - " + super.toString();
	}

	public void setCar(Car car) {
		this.car = car;
	}
}
