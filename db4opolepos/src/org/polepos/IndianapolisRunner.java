/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos;

import org.polepos.circuits.indianapolis.*;
import org.polepos.db4o.*;
import org.polepos.framework.*;
import org.polepos.teams.db4o.*;

public class IndianapolisRunner extends AbstractDb4oVersionsRaceRunner {
	public static void main(String[] arguments) {
        new IndianapolisRunner().run();
    }
	
	public Circuit[] circuits() {
		return new Circuit[] { 
				new Indianapolis(),  
		};
	}
	
	public Team[] teams(){
		return new Team [] {
				db4oTeam(Db4oVersions.JAR60, null),		
				db4oTeam(null, null),
		};
	}

	public Driver[] drivers() {
		return new Driver [] {
				new IndianapolisDb4o(),
		};
	}
	
}
