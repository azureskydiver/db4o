/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos;

import org.polepos.circuits.collection.*;
import org.polepos.db4o.*;
import org.polepos.framework.*;
import org.polepos.teams.db4o.*;

public class CollectionRunner extends AbstractDb4oVersionsRaceRunner {
	public static void main(String[] arguments) {
        new CollectionRunner().run();
    }
	
	public Circuit[] circuits() {
		return new Circuit[] { 
				new Collection(),  
		};
	}
	
	public Team[] teams(){
		return new Team [] {
				db4oTeam(Db4oVersions.JAR60, new int[]{Db4oOptions.NORMAL_COLLECTION}),		
				db4oTeam(Db4oVersions.JAR60, new int[]{Db4oOptions.P1FAST_COLLECTION}),
				db4oTeam(null, new int[]{Db4oOptions.NORMAL_COLLECTION}),
				db4oTeam(null, new int[]{Db4oOptions.P1FAST_COLLECTION}),
		};
	}

	public Driver[] drivers() {
		return new Driver [] {
				new CollectionDriverImpl(),
		};
	}
	
}
