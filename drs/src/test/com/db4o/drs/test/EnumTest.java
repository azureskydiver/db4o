package com.db4o.drs.test;

import com.db4o.drs.inside.TestableReplicationProviderInside;
import db4ounit.Assert;

/**
 * Run on JDK 5 only. Run with db4o only, not with Hibernate
 */
public class EnumTest extends DrsTestCase {
	public void test() {
		storeToA();
		replicateToB();
		modifyInB();
		replicateToA();
		modifyInA();
		replicateToBSecondTime();
	}

	@SuppressWarnings("serial")
	private void storeToA() {
		Teacher t = new Teacher("Albert Kwan", Qualification.TRAINEE);

		final TestableReplicationProviderInside provider = a().provider();

		provider.storeNew(t);
		provider.commit();

		ensureContent(provider, "Albert Kwan", Qualification.TRAINEE);
	}

	private void replicateToB() {
		replicateAll(a().provider(), b().provider());

		ensureContent(a().provider(), "Albert Kwan", Qualification.TRAINEE);
		ensureContent(b().provider(), "Albert Kwan", Qualification.TRAINEE);
	}

	private void modifyInB() {
		final TestableReplicationProviderInside provider = b().provider();

		Teacher out = (Teacher) getOneInstance(provider, Teacher.class);
		out.name = "Audi R8";
		out.qualification = Qualification.PROFESSIONAL;

		provider.update(out);
		provider.commit();

		ensureContent(b().provider(), "Audi R8", Qualification.PROFESSIONAL);
	}

	private void replicateToA() {
		replicateAll(b().provider(), a().provider());

		ensureContent(a().provider(), "Audi R8", Qualification.PROFESSIONAL);
		ensureContent(b().provider(), "Audi R8", Qualification.PROFESSIONAL);
	}

	private void modifyInA() {
		final TestableReplicationProviderInside provider = a().provider();

		Teacher out = (Teacher) getOneInstance(provider, Teacher.class);
		out.name = "Honda Civic Type-R";
		out.qualification = Qualification.WINNER;

		provider.update(out);
		provider.commit();

		ensureContent(a().provider(), "Honda Civic Type-R",
				Qualification.WINNER);
	}

	private void replicateToBSecondTime() {
		replicateAll(a().provider(), b().provider());

		ensureContent(a().provider(), "Honda Civic Type-R",
				Qualification.WINNER);
		ensureContent(b().provider(), "Honda Civic Type-R",
				Qualification.WINNER);
	}

	private void ensureContent(TestableReplicationProviderInside provider,
			String teacherName, Qualification teacherQuali) {
		Teacher out = (Teacher) provider.getStoredObjects(Teacher.class).next();
		Assert.isNotNull(out);
		Assert.areEqual(teacherName, out.name);
		Assert.areEqual(teacherQuali, out.qualification);
	}
}

class Teacher {
	public String name;

	public Qualification qualification;

	public Teacher(String name, Qualification qualification) {
		this.name = name;
		this.qualification = qualification;
	}

	public String toString() {
		return name + "/" + qualification;
	}
}

enum Qualification {
	WINNER("WINNER"), PROFESSIONAL("PROFESSIONAL"), TRAINEE("TRAINEE");

	private String qualification;

	private Qualification(String qualification) {
		this.qualification = qualification;
	}

	public String toString() {
		return qualification;
	}
}