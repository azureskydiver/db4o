/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

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
package com.db4o.test.replication.old;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;
import com.db4o.tools.*;

public class ReplicateExistingFile {
    
    public static class Task {
        
        public String _name;

        public Task(String name) {
            _name = name;
        }
        
        public String name() {
            return _name;
        }
    }
	
	static final ReplicationConflictHandler _handler = new ReplicationConflictHandler() {
		public Object resolveConflict(ReplicationProcess replicationProcess, Object a, Object b) {
			return a;
		}
	};
	
	public void configure() {
		Db4o.configure().objectClass(Task.class).enableReplication(false);
	}
	
	public void store() {
		Test.store(new Task("old task 1"));
		Test.store(new Task("old task 2"));
	}

	public void test() {
		
		close();
		
		try {
			Db4o.configure().objectClass(Task.class).enableReplication(true);
			new Defragment().run(currentFileName(), true);
		} finally {
			Db4o.configure().objectClass(Task.class).enableReplication(false);
			reOpen();
		}
		
		ObjectContainer master = Test.objectContainer();
		ObjectContainer slave = Test.replica();
		
		replicate(master, slave);
		
		Query q = slave.query();
		q.constrain(Task.class);
		ObjectSet os = q.execute();
		Test.ensure(2 == os.size());
		
		
	}
	
	void replicate(ObjectContainer master, ObjectContainer slave) {
		ReplicationProcess replication = master.ext().replicationBegin(slave, _handler);
		
		ObjectSet replicationSet = objectsToReplicate(replication, master);
        while (replicationSet.hasNext()) {
            replication.replicate(replicationSet.next());
        }
        replication.commit();
	}
	
	ObjectSet objectsToReplicate(ReplicationProcess replication, ObjectContainer master) {
        // replicate all modified objects
        Query q = master.query();
        replication.whereModified(q);
        return q.execute();
	}
	
	private String currentFileName() {
		return Test.isClientServer()
			? Test.FILE_SERVER
			: Test.FILE_SOLO;
	}
	
	private void close() {
		Test.close();
		if (Test.isClientServer()) {
			Test.server().close();
		}
	}
	
	private void reOpen() {
		Test.reOpenServer();
	}
}