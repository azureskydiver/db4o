/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;


public class InheritanceTest extends DrsTestCase {
	
	private static final String NEW_NAME = "Jeanette";
	private static final String NEW_STUDENTNO = "VANJ";
	private static final int NEW_AGE = 41;
	private static final int OLD_AGE = 37;
	private static final String OLD_STUDENTNO = "VANP";
	private static final String OLD_NAME = "Peter";

	public void Test() {
		Store();
		Replicate(a().provider(), b().provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
		ModifyInB();
		Replicate(b().provider(), a().provider(), NEW_NAME, NEW_STUDENTNO, NEW_AGE);
	}

	private void ModifyInB() {
		Student student = getTheStudent(b().provider());
		ensureOneInstance(b().provider(), Person.class);
		student.setAge(NEW_AGE);
		student.setStudentNo(NEW_STUDENTNO);
		student.setName(NEW_NAME);
		b().provider().update(student);
		b().provider().commit();
		ensureDetails(b().provider(), NEW_NAME, NEW_STUDENTNO, NEW_AGE); 
		ensureDetails(a().provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
	}

	private void Replicate(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo, String name, String studentno, int age) {
		replicateAll(providerFrom, providerTo);
		ensureDetails(providerFrom, name, studentno, age);
		ensureDetails(providerTo, name, studentno, age);
	}

	private void Store() {
		Student _student = new Student(OLD_NAME, OLD_AGE);
		_student.setStudentNo(OLD_STUDENTNO);
		a().provider().storeNew(_student);
		a().provider().commit();
		ensureDetails(a().provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
	}
	
	private void ensureDetails(TestableReplicationProviderInside provider, String name, String studentno, int age) {
		ensureOneInstance(provider, Person.class);
		ensureOneInstance(provider, Student.class);
		Person person = getThePerson(provider);
		Assert.areEqual(name, person.getName());
		Assert.areEqual(age, person.getAge());
		Student student = getTheStudent(provider);
		Assert.areEqual(studentno, student.getStudentNo());
		Assert.areEqual(name, student.getName());
		Assert.areEqual(age, student.getAge());
	}

	private Person getThePerson(TestableReplicationProviderInside provider) {
		return (Person) getOneInstance(provider, Person.class);
	}

	private Student getTheStudent(TestableReplicationProviderInside provider) {
		return (Student) getOneInstance(provider, Student.class);
	}
}
