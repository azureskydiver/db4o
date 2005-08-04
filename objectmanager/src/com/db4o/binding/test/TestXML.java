package com.db4o.binding.test;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;


public class TestXML {
	
	static class Person {
		private String name;
		private List toys = new ArrayList();
		public Person(String name) {
			this.name = name;
		}
		public void addToy(Object toy) {
			toys.add(toy);
		}
	}
	
	static class Computer {
		String type;

		public Computer(String type) {
			this.type = type;
		}
	}
	
	static class Car {
		Person owner;
		String make;
		public Car(String make, Person owner) {
			this.make = make;
			this.owner = owner;
		}
	}
	
	public static void main(String[] args) {
		XStream xstream = new XStream();
		
		Person person = new Person("Joe");
		person.addToy(new Computer("Dell"));
		person.addToy(new Computer("Beowulf cluster"));
		person.addToy(new Car("Ford", person));
		
		System.out.println(xstream.toXML(person));
	}

}
