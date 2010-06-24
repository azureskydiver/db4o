/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

package org.polepos;


import org.polepos.circuits.bahrain.*;
import org.polepos.circuits.barcelona.*;
import org.polepos.circuits.hockenheim.*;
import org.polepos.circuits.hungaroring.*;
import org.polepos.circuits.imola.*;
import org.polepos.circuits.indianapolis.*;
import org.polepos.circuits.istanbul.*;
import org.polepos.circuits.magnycours.*;
import org.polepos.circuits.melbourne.*;
import org.polepos.circuits.monaco.*;
import org.polepos.circuits.montreal.*;
import org.polepos.circuits.nurburgring.*;
import org.polepos.circuits.sepang.*;
import org.polepos.circuits.silverstone.*;
import org.polepos.framework.*;
import org.polepos.reporters.*;
import org.polepos.runner.db4o.*;
import org.polepos.teams.db4o.*;

import com.db4o.config.*;
import com.db4o.configurations.*;
import com.db4o.internal.*;
import com.db4o.internal.caching.*;
import com.db4o.internal.config.*;
import com.db4o.io.*;

/**
 * Please read the README file in the home directory first.
 */
public class AllRacesRunner extends AbstractDb4oVersionsRaceRunner{
    
    private static String JAR_TRUNK = "db4o-7.10.103.13376-all-java5.jar";

    private static String JAR_INTCB = "db4o-7.10.103.13376-all-java5.intcb.jar";

    private static String JAR_DEVEL = "db4o-8.0.145.14388-all-java5.jar";
    
    private static String JAR_STABLE = "db4o-7.4.136.14268-java5.jar";
    
    private static String JAR_PRODUCTION = "db4o-7.12.145.14388-all-java5.jar";
    
    public static void main(String[] arguments) {
        new AllRacesRunner().run();
    }
    
    @Override
    protected Reporter[] reporters() {
    	//return new Reporter[] {new LoggingReporter()};
    	return DefaultReporterFactory.defaultReporters();
    }
    
    public Team[] teams() {

		return new Team[] {
				
				// db4oTeam(JAR_TRUNK),
				
				configuredDb4oTeam(JAR_DEVEL),
				configuredDb4oTeam(JAR_STABLE),
				configuredDb4oTeam(JAR_PRODUCTION),
//				configuredDb4oTeam(JAR_DEVEL, new SingleBTreeIdSystem()),
//				configuredDb4oTeam(JAR_DEVEL, new PointerBasedIdSystem()),
				
//				db4oTeam(JAR_PRODUCTION),
//				
//				// configuredDb4oTeam(JAR_PRODUCTION, new BTreeFreespaceManager()),
//				
//				db4oTeam(JAR_STABLE),
//				
//				db4oTeam(JAR_DEVEL, new int[]{Db4oOptions.CLIENT_SERVER_TCP, Db4oOptions.CLIENT_SERVER}),
//				db4oTeam(JAR_PRODUCTION, new int[]{Db4oOptions.CLIENT_SERVER_TCP, Db4oOptions.CLIENT_SERVER}),
//				db4oTeam(JAR_STABLE, new int[]{Db4oOptions.CLIENT_SERVER_TCP, Db4oOptions.CLIENT_SERVER}),
				
		};
	}
    
    private ConfigurationSetting bTreeIdSystem(){
    	return new ConfigurationSetting() {
			
			public String name() {
				return "BTreeIdSystem";
			}
			
			public void apply(Object config) {
				Db4oLegacyConfigurationBridge.asIdSystemConfiguration((Config4Impl)config).useStackedBTreeSystem();
			}
		};
    	
    }
    
    private ConfigurationSetting fileBasedTransactionLog(){
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Config4Impl)config).fileBasedTransactionLog(true);
			}
			public String name() {
				return "CachedIoAdapter";
			}
			
		};
    }

	private ConfigurationSetting cachedIoAdapter() {
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Configuration)config).io(new CachedIoAdapter(new RandomAccessFileAdapter()));
			}
			public String name() {
				return "TransactionLog";
			}
			
		};
	}

	private ConfigurationSetting lru() {
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Configuration)config).storage(new CachingStorage(new FileStorage()));
			}
			public String name() {
				return "NewLRU";
			}
		};
	}

	private ConfigurationSetting lRU2Q() {
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Configuration)config).storage(new CachingStorage(new FileStorage()){
					@Override
					protected Cache4 newCache() {
						return CacheFactory.new2QCache(30);
					}
					
				});
			}
			public String name() {
				return "LRU2Q";
			}
		};
	}

	private ConfigurationSetting slotCache(final int slotCacheSize) {
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Configuration)config).cache().slotCacheSize(slotCacheSize);
			}
			public String name() {
				return "" + slotCacheSize + " slotCacheSize";
			}
		};
	}

	private ConfigurationSetting randomAccessFileAdapter() {
		return new ConfigurationSetting(){
			public void apply(Object config) {
				((Configuration)config).io(new RandomAccessFileAdapter());
				
			}
			public String name() {
				return "RandomAccessFileAdapter";
			}
			
		};
	}
    
    

	public CircuitBase[] circuits() {
		return new CircuitBase[] {
//			 new Melbourne(),
//			 new Sepang(),
//			 new Bahrain(),
//			 new Imola(),
//			 new Barcelona(),
//			 new Monaco(),
//			 new Nurburgring(),
//			 new Montreal(),
//			 new IndianapolisFast(),
//			 new IndianapolisMedium(),
//			 new IndianapolisSlow(),
//			 new Magnycours(),
			 new Silverstone(),
//			 new Hockenheim(),
//			 new Hungaroring(),
//			 new Istanbul(),
		};
	}

	public DriverBase[] drivers() {
		return new DriverBase [] {
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
			new HockenheimDb4o(),
			new HungaroringDb4o(),
			new IstanbulDb4o(),
		};
	}
    
}
