package com.db4o.nativequery.example;

public class Student {
	public String name;
	public int age;
	public Student tortue;

	public Student(int age, String name) {
		this(age,name,null);
	}

	public Student(int age, String name,Student tortue) {
		this.age = age;
		this.name = name;
		this.tortue=tortue;
	}

	public int getAge() {
		return age/*-1*/; // TODO
	}

	public String getName() {
		return name;
	}

	public Student getTortue() {
		return tortue;
	}

	public String toString() {
		return name+"/"+age+"/"+tortue;
	}
}
