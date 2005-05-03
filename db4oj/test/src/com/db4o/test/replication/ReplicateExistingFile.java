package com.db4o.test.replication;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.replication.ReplicationConflictHandler;
import com.db4o.replication.ReplicationProcess;
import com.db4o.test.Test;
import com.db4o.tools.Defragment;

class Task {
	private String _name;

	public Task(String name) {
		_name = name;
	}
	
	public String name() {
		return _name;
	}
}

public class ReplicateExistingFile {
	
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