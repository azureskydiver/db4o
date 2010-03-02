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
				 new IndianapolisFast(),
				 new Magnycours(),
                 new Silverstone(),
		};
	}

	public Driver[] drivers(Db4oEngine engine) {
		return new Driver [] {
				new MelbourneDb4o(engine),
		        new SepangDb4o(engine),
		        new BahrainDb4o(engine),
		        new ImolaDb4o(engine),
		        new BarcelonaDb4o(engine),
		        new MonacoDb4o(engine),
		        new NurburgringDb4o(engine),
		        new MontrealDb4o(engine),
				new MagnycoursDb4o(engine),
				new IndianapolisDb4o(engine),
                new SilverstoneDb4o(engine),
		};
	}



}
