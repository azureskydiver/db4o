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
package f1.singleobject;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.ext.ExtDb4o;

import org.hibernate.cfg.Configuration;

import java.io.File;

public class SingleObjectExample {

	public static void main(String[] args) {
		new File("SingleObjectExample.yap").delete();

		System.out.println("Running SingleObjectExample example.");
		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer objectContainer = Db4o.openFile("SingleObjectExample.yap");

		Pilot pilot = new Pilot();
		pilot.name = "John";
		pilot.points = 9;
		objectContainer.store(pilot);
		objectContainer.commit();

		Configuration config = new Configuration().configure("f1/singleobject/hibernate.cfg.xml");

		ReplicationSession replication = HibernateReplication.begin(objectContainer, config);
		ObjectSet it = replication.providerA().objectsChangedSinceLastReplication();

		while (it.hasNext()) {
			Object o = it.next();
			replication.replicate(o);
		}

		replication.commit();

		objectContainer.close();

		new File("SingleObjectExample.yap").delete();
	}
}
