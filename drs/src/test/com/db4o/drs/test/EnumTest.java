/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test;

import com.db4o.drs.inside.TestableReplicationProviderInside;
import db4ounit.Assert;

/**
 * Run on JDK 5 only. Run with db4o only, not with Hibernate
 * 
 * @sharpen.ignore
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

		Teacher out = (Teacher) getOneInstance(b(), Teacher.class);
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

		Teacher out = (Teacher) getOneInstance(a(), Teacher.class);
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

/**
 * @sharpen.ignore
 */
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

/**
 * @sharpen.ignore
 */
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