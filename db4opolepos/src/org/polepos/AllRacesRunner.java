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
import org.polepos.runner.db4o.*;
import org.polepos.teams.db4o.*;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.caching.*;
import com.db4o.io.*;

/**
 * Please read the README file in the home directory first.
 */
public class AllRacesRunner extends AbstractDb4oVersionsRaceRunner{
    
    private static String JAR_TRUNK = "db4o-trunk-java5.jar";
    
    private static String JAR70 = "db4o-7.0.21.8746-java5.jar";
    
    private static String JAR72 = "db4o-7.2.39.10644-java5.jar";
    
    public static void main(String[] arguments) {
        new AllRacesRunner().run();
    }
    
    public Team[] teams() {

		return new Team[] {
				
				db4oTeam(),
				configuredDb4oTeam(new ConfigurationSetting[] { 
				fileBasedTransactionLog(),
				}),
				
//				configuredDb4oTeam(new ConfigurationSetting[] { 
//						slotCache(0),
//						randomAccessFileAdapter(), }),
//				configuredDb4oTeam(new ConfigurationSetting[] { 
//						slotCache(30),
//						randomAccessFileAdapter(), }),
//				configuredDb4oTeam(new ConfigurationSetting[] { 
//						slotCache(0),
//						cachedIoAdapter(), }),
//				configuredDb4oTeam(new ConfigurationSetting[] { 
//						slotCache(30),
//						cachedIoAdapter(), }),
						
            
// configuredDb4oTeam(new ConfigurationSetting[]{
// cachedIoAdapter()
// }),
//            

            
// db4oTeam(JAR_TRUNK, null),
// db4oTeam(Db4oVersions.JAR63, new int[] {Db4oOptions.CLIENT_SERVER,
// Db4oOptions.CLIENT_SERVER_TCP }),
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
    
    

	public Circuit[] circuits() {
		return new Circuit[] {
//			 new Melbourne(),
//			 new Sepang(),
//			 new Bahrain(),
//			 new Imola(),
//			 new Barcelona(),
//			 new Monaco(),
//			 new Nurburgring(),
//			 new Montreal(),
//			 new Indianapolis(),
			 new Magnycours(),
//			 new Silverstone(),
//			 new Hockenheim(),
//			 new Hungaroring(),
//			 new Istanbul(),
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
			new HockenheimDb4o(),
			new HungaroringDb4o(),
			new IstanbulDb4o(),
		};
	}
    
}
