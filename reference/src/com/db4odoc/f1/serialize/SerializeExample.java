/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.serialize;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.f1.Util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class SerializeExample extends Util {

	public static String XMLFILENAME = "formula1.xml";
	
	public static void main(String[] args) {
		setObjects();
		exportToXml();
		importFromXml();
	}
	
	public static void setObjects(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			db.set(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			db.set(car);
		} finally {
			db.close();
		}
	}
	
	public static void exportToXml()
	{
		XStream xstream = new XStream(new DomDriver());
		try {
			FileWriter xmlFile = new FileWriter(XMLFILENAME);
			ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
			try 
			{
				ObjectSet result = db.query(Car.class);
				Car[] cars = new Car[result.size()];
				for (int i = 0; i < result.size(); i++)
				{
					Car car = (Car)result.next();
					cars[i] = car;
				}
				String xml = xstream.toXML(cars);
				xmlFile.write("<?xml version=\"1.0\"?>\n"+xml);
				xmlFile.close();
			}
			finally
			{
				db.close();
			}
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public static void importFromXml() {
		new File(Util.YAPFILENAME).delete();
		XStream xstream = new XStream(new DomDriver());
		try {
			FileReader xmlReader = new FileReader(XMLFILENAME);
			Car[] cars = (Car[]) xstream.fromXML(xmlReader);
			ObjectContainer db;
			for (int i = 0; i < cars.length; i++) {
				db = Db4o.openFile(Util.YAPFILENAME);
				try {
					Car car = (Car) cars[i];
					db.set(car);
				} finally {
					db.close();
				}
			}
			db = Db4o.openFile(Util.YAPFILENAME);
			try {
				ObjectSet result = db.query(Pilot.class);
				listResult(result);
				result = db.query(Car.class);
				listResult(result);
			} finally {
				db.close();
			}
			xmlReader.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	
}
