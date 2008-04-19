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
package company;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ObjectState;
import com.db4o.drs.ReplicationEvent;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;

import org.hibernate.cfg.Configuration;

import java.util.List;

public class Example {
	public static void main(String[] args) {

		//Open the db4o database
		ObjectContainer objectContainer = Db4o.openFile("company.yap");

		//Read the Hibernate Config file (in the classpath)
		Configuration hibernateConfiguration = new Configuration().configure("hibernate.cfg.xml");

//Start a Replication Session
		ReplicationEventListener listener;
		listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				if (event.stateInProviderA().getObject() instanceof List)
					event.stopTraversal();
			}
		};

		listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				if (event.isConflict()) {
					ObjectState chosenObjectState = event.stateInProviderA();
					event.overrideWith(chosenObjectState);
				}
			}
		};

		ReplicationSession replication = HibernateReplication.begin(objectContainer, hibernateConfiguration, listener);

		//Query for changed objects
		ObjectSet changedObjects = replication.providerB().objectsChangedSinceLastReplication();

		//One-line-of-code replication
		while (changedObjects.hasNext())
			replication.replicate(changedObjects.next());

		//Commit
		replication.commit();
	}


}
