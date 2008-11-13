package org.polepos;

import org.polepos.circuits.bahrain.*;
import org.polepos.circuits.barcelona.*;
import org.polepos.circuits.imola.*;
import org.polepos.circuits.indianapolis.*;
import org.polepos.circuits.magnycours.*;
import org.polepos.circuits.melbourne.*;
import org.polepos.circuits.monaco.*;
import org.polepos.circuits.montreal.*;
import org.polepos.circuits.nurburgring.*;
import org.polepos.circuits.sepang.*;
import org.polepos.circuits.silverstone.*;
import org.polepos.framework.*;
import org.polepos.runner.db4o.*;
import org.polepos.teams.db4o.*;

public class PerformanceCompetition extends AbstractDb4oVersionsRaceRunner{

	public static void main(String[] args) {
		new PerformanceCompetition().run();
	}
	
    private static String JAR72 = "db4o-7.2.39.10644-java5.jar";
    
    private static String JAR_PATCHED = "db4o-7.2-patched.jar";
    
    public Team[] teams() {

		return new Team[] {
            db4oTeam(JAR72),
            db4oTeam(JAR_PATCHED),
            db4oTeam(JAR72, new int[] {Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP }),
            db4oTeam(JAR_PATCHED, new int[] {Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP }),
		};
	}

	public Circuit[] circuits() {
		return new Circuit[] { 
				 new Melbourne(),
				 new Sepang(),
				 new Bahrain(),
				 new Imola(),
				 new Barcelona(),
				 new Monaco(),
				 new Nurburgring(),
				 new Montreal(),
				 new Indianapolis(),
				 new Magnycours(),
                 new Silverstone(),
		};
	}

	public Driver[] drivers() {
		return new Driver [] {
				new MelbourneDb4o(),
		        new SepangDb4o(),
		        new BahrainDb4o(),
		        new ImolaDb4o(),
		        new BarcelonaDb4o(),
		        new MonacoDb4o(),
		        new NurburgringDb4o(),
		        new MontrealDb4o(),
				new MagnycoursDb4o(),
				new IndianapolisDb4o(),
                new SilverstoneDb4o(),
		};
	}



}
