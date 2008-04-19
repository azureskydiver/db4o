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
package f1.updateevent;

import com.db4o.drs.hibernate.ReplicationConfigurator;

import f1.collection.Car;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UpdateEventExample {
	public static void main(String[] args) {
		System.out.println("Running UpdateEvent example.");

		Pilot pilot = new Pilot();
		pilot.name = "John";

		Configuration cfg = new Configuration().configure("f1/updateevent/hibernate.cfg.xml");
		ReplicationConfigurator.configure(cfg);

		SessionFactory sessionFactory = cfg.buildSessionFactory();
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.install(session, cfg);

		session.save(pilot);

		pilot.name = "changed";
		session.flush();

		Car car1 = new Car();
		car1.brand = "BMW";
		car1.model = "M3";

		pilot.cars.add(car1);
		session.flush();

		Car car2 = new Car();
		car2.brand = "Mercedes Benz";
		car2.model = "S600SL";
		pilot.cars.add(car2);
		session.flush();

		pilot.cars.remove(car2);
		session.flush();

		session.close();
	}
}