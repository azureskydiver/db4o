/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

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
package f1.collection.list;


import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.*;
import com.db4o.drs.db4o.*;
import com.db4o.drs.hibernate.impl.*;

import f1.*;
import f1.collection.Car;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

public class ListExample extends ExampleBase {
	
	public static void main(String[] args) {
		new ListExample().run();
	}

	public void run() {
		deleteDb4oDatabaseFile();

		System.out.println("Running List example.");
		
		ObjectContainer objectContainer = openObjectContainer(db4oFileName());

		Pilot pilot = new Pilot();
		pilot.name = "John";

		Car car1 = new Car();
		car1.brand = "BMW";
		car1.model = "M3";

		Car car2 = new Car();
		car2.brand = "Mercedes Benz";
		car2.model = "S600SL";

		pilot.cars = new ArrayList();
		pilot.cars.add(car1);
		pilot.cars.add(car2);

		objectContainer.store(pilot);
		objectContainer.commit();
		

		String string = "f1/collection/list/hibernate.cfg.xml";
		Configuration config = new Configuration().configure(string);
		
		Db4oEmbeddedReplicationProvider providerA = new Db4oEmbeddedReplicationProvider(objectContainer);
		HibernateReplicationProvider providerB = new HibernateReplicationProvider(config);
		
		ReplicationSession replication = Replication.begin(providerA, providerB);

		ObjectSet changed = replication.providerA().objectsChangedSinceLastReplication();

		while (changed.hasNext())
			replication.replicate(changed.next());

		replication.commit();
		replication.close();
		objectContainer.close();

		deleteDb4oDatabaseFile();
	}
}