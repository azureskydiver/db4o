package model;

import java.io.Serializable;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 5:06:07 PM
 */
public class Car implements Serializable {
	private String name;

	public Car(String name) {

		this.name = name;
	}

	public Car() {

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
