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


import java.io.*;
import java.util.*;

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
import org.polepos.circuits.sepangmulti.*;
import org.polepos.circuits.silverstone.*;
import org.polepos.framework.*;
import org.polepos.reporters.*;
import org.polepos.runner.db4o.*;
import org.polepos.teams.db4o.*;

import com.db4o.polepos.continuous.*;

/**
 * Please read the README file in the home directory first.
 */
public class PerformanceMonitoringRunner extends AbstractDb4oVersionsRaceRunner{
	
	private static final String SETTINGS_FILE = "settings/PerfCircuits.properties";

	private final Db4oJarCollection _jarCollection;
	private final PerformanceMonitoringReporter _reporter;
	
    public static void main(String[] args) {
        System.exit(new PerformanceMonitoringRunner(toFiles(args)).runMonitored());
    }

    private static File[] toFiles(String[] paths) {
    	File[] files = new File[paths.length];
    	for (int pathIdx = 0; pathIdx < paths.length; pathIdx++) {
			files[pathIdx] = new File(paths[pathIdx]).getAbsoluteFile();
			if(!files[pathIdx].exists() || !files[pathIdx].isDirectory()) {
				throw new IllegalArgumentException("Not a directory: " + files[pathIdx]);
			}
		}
    	return files;
    }
    
    public int runMonitored() {
    	run(SETTINGS_FILE);
    	PerformanceReport report = _reporter.performanceReport();
    	report.print(new OutputStreamWriter(System.err));
    	return report.performanceOk() ? 0 : -99;
    }

    public PerformanceMonitoringRunner() {
    	this(null);
    }

    public PerformanceMonitoringRunner(File[] libPaths) {
    	_jarCollection = new FolderBasedDb4oJarRegistry(libPaths == null ? libPaths() : libPaths, new RevisionBasedMostRecentJarFileSelectionStrategy(1)).jarCollection();
    	_reporter = new PerformanceMonitoringReporter(_jarCollection.currentJar().getName(), new SpeedTicketPerformanceStrategy(20));
    }
    
    @Override
    protected Reporter[] reporters() {
    	Reporter[] defaultReporters = DefaultReporterFactory.defaultReporters();
    	Reporter[] allReporters = new Reporter[defaultReporters.length + 2];
    	allReporters[0] = _reporter;
    	allReporters[1] = new StdErrLoggingReporter();
    	System.arraycopy(defaultReporters, 0, allReporters, 2, defaultReporters.length);
    	return allReporters;
    }
    
    public Team[] teams() {
    	Set<Team> teams = new HashSet<Team>();
    	teams.add(db4oTeam(_jarCollection.currentJar().getName()));
    	for (File otherJar : _jarCollection.otherJars()) {
			teams.add(db4oTeam(otherJar.getName()));
		}
    	return teams.toArray(new Team[teams.size()]);
	}

	public Circuit[] circuits() {
		return new Circuit[] {
			 new Melbourne(),
			 new SepangMulti(),
			 new Sepang(),
			 new Bahrain(),
			 new Imola(),
			 new Barcelona(),
			 new Monaco(),
			 new Nurburgring(),
			 new Montreal(),
			 new Indianapolis(),
			 new IndianapolisUnoptimized(),
			 new Magnycours(),
			 new Silverstone(),
			 new Hockenheim(),
			 new Hungaroring(),
			 new Istanbul(),
		};
	}

	public Driver[] drivers() {
		return new Driver [] {
			new MelbourneDb4o(),
			new SepangMultiDb4o(),
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
