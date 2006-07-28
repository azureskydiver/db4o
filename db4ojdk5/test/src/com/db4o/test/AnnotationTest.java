/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;

public class AnnotationTest {

	public static void main(String[] args) {
		MemoryFile file = new MemoryFile();
		ObjectContainer db = ExtDb4o.openMemoryFile(file);
		try {
			fillDBwithSheeps(db);
			db = ExtDb4o.openMemoryFile(file);
			retriveSheep(db);

			db = ExtDb4o.openMemoryFile(file);
			System.out.println("\n");
			updateSheep(db);

			db = ExtDb4o.openMemoryFile(file);
			System.out.println("\n");
			deleteSheep(db);

		} finally {
			db.close();
		}
	}

	private static void updateSheep(ObjectContainer db) {
		System.out.println("updating annotated sheeps");
		Sheep sheep = (Sheep) db.get(new Sheep("test7", null)).next();
		sheep.setName(sheep.getName() + "_new");
		db.set(sheep);
		ObjectSet<Sheep> result = db.get(Sheep.class);
		while (result.hasNext()) {
			System.out.println(result.next());
		}

		System.out.println("updating not annotated sheeps");
		SheepNotAnnotated notAnnot = (SheepNotAnnotated) db.get(
				new SheepNotAnnotated("notAnnotTest7", null)).next();
		notAnnot.setName(notAnnot.getName() + "_new");
		db.set(notAnnot);
		ObjectSet<SheepNotAnnotated> res = db.get(SheepNotAnnotated.class);
		while (res.hasNext()) {
			System.out.println(res.next());

		}

		db.close();
	}

	private static void deleteSheep(ObjectContainer db) {
		System.out.println("deleting annotated sheeps");
		Sheep sheep = (Sheep) db.get(new Sheep("test15", null)).next();
		db.delete(sheep);
		ObjectSet<Sheep> result = db.get(Sheep.class);
		while (result.hasNext()) {
			System.out.println(">>" + (Sheep) result.next());
		}
		System.out.println("deleting not annotated sheeps");
		SheepNotAnnotated notAnnot = (SheepNotAnnotated) db.get(
				new SheepNotAnnotated("notAnnotTest15", null)).next();
		db.delete(notAnnot);
		ObjectSet<SheepNotAnnotated> res = db.get(SheepNotAnnotated.class);
		while (res.hasNext()) {
			System.out.println(">>>" + (SheepNotAnnotated) res.next());
		}
		db.commit();
		db.close();
	}

	private static void retriveSheep(ObjectContainer db) {
		System.out.println("retriving annotated sheeps");
		ObjectSet<Sheep> result = db.get(new Sheep("test23", null));
		System.out.println((Sheep) result.next());

		System.out.println("retriving not annotated sheeps");
		ObjectSet<SheepNotAnnotated> res = db.get(new SheepNotAnnotated(
				"notAnnotTest23", null));
		System.out.println((SheepNotAnnotated) res.next());
		db.close();
	}

	private static void fillDBwithSheeps(ObjectContainer db) {
		Sheep parent = null;
		for (int i = 0; i < 36; i++) {
			Sheep sheep = new Sheep("test" + i, parent);
			db.set(sheep);
			parent = sheep;
		}

		SheepNotAnnotated notAnnotParent = null;
		for (int j = 0; j < 36; j++) {
			SheepNotAnnotated notAnnotChild = new SheepNotAnnotated(
					"notAnnotTest" + j, notAnnotParent);
			db.set(notAnnotChild);
			notAnnotParent = notAnnotChild;
		}

		db.commit();
		db.close();
	}

}
