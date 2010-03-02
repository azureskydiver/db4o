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
import com.db4o.polepos.continuous.filealgebra.*;

/**
 * Please read the README file in the home directory first.
 */
public class PerformanceMonitoringRunner extends AbstractDb4oVersionsRaceRunner{
	
	private static final int PERFORMANCE_PERCENTAGE_THRESHOLD = 5;

	private static final String SETTINGS_FILE = "settings/PerfCircuits.properties";

	private static final int NUM_RUNS = 11;

	private final Db4oJarCollection _jarCollection;
	private final PerformanceMonitoringReporter[] _reporters;
	
    public static void main(String[] args) {
    	System.setProperty(Circuit.NUM_RUNS_PROPERTY_ID, String.valueOf(NUM_RUNS));
    	System.setProperty(Circuit.MEMORY_USAGE_PROPERTY_ID, Circuit.NullMemoryUsage.class.getName());
    	int[] selectedIndices = null;
    	try {
    		selectedIndices = parseSelectedIndices(args[0]);
    	}
    	catch(NumberFormatException exc) {
    		System.err.println("Usage: PerformanceMonitoringRunner <selected indices, comma separated> <fixed unconditional jar folder> <jar folder paths, space separated>");
    		throw exc;
    	}
    	File[] folders = toFiles(extractFileArgs(args));
    	File[] libPaths = new File[folders.length - 1];
    	System.arraycopy(folders, 1, libPaths, 0, libPaths.length);
		System.exit(new PerformanceMonitoringRunner(selectedIndices, folders[0], libPaths).runMonitored());
    }

	private static String[] extractFileArgs(String[] args) {
		String[] files = new String[args.length - 1];
    	System.arraycopy(args, 1, files, 0, files.length);
		return files;
	}

    private static int[] parseSelectedIndices(String selectedIdxStr) {
    	String[] selectedIdxStrArr = selectedIdxStr.split(",");
    	int[] selectedIndices = new int[selectedIdxStrArr.length];
    	for (int selIdxIdx = 0; selIdxIdx < selectedIndices.length; selIdxIdx++) {
			selectedIndices[selIdxIdx] = Integer.parseInt(selectedIdxStrArr[selIdxIdx]);
		}
		return selectedIndices;
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
    	boolean performanceOk = true;
    	for (PerformanceMonitoringReporter reporter : _reporters) {
    		PerformanceReport report = reporter.performanceReport();
    		report.print(new OutputStreamWriter(System.err));
    		performanceOk &= report.performanceOk();
		}
    	return performanceOk ? 0 : -99;
    }

    public PerformanceMonitoringRunner(int[] selectedIndices, File fixedFolder, File[] libPaths) {
    	List<FileSource> sources = new ArrayList<FileSource>();
    	
    	for (File libPath : libPaths) {
			sources.add(new FolderFileSource(libPath));
		}
    	FileSource compositeSource = new CompositeFileSource(sources);
    	FileSource filteredSource = new Db4oJarSortedFileSource(compositeSource);
    	File recentJar = new TakeFirstSingleFileSource(filteredSource).file();
    	FileSource flexibleJarSource = new LenientIndexSelectingFileSource(filteredSource, selectedIndices);
		List<File> otherJars = new CompositeFileSource(flexibleJarSource, new Db4oJarSortedFileSource(new FolderFileSource(fixedFolder))).files();
    	_jarCollection = new Db4oJarCollection(recentJar, otherJars);
    	_reporters = new PerformanceMonitoringReporter[] {
    			new PerformanceMonitoringReporter(_jarCollection.currentJar().getName(), MeasurementType.TIME, new SpeedTicketPerformanceStrategy(PERFORMANCE_PERCENTAGE_THRESHOLD)),
    			//new PerformanceMonitoringReporter(_jarCollection.currentJar().getName(), MeasurementType.MEMORY, new SpeedTicketPerformanceStrategy(PERFORMANCE_PERCENTAGE_THRESHOLD)),
    	};
    }
    
    @Override
    protected Reporter[] reporters() {
    	Reporter[] defaultReporters = DefaultReporterFactory.defaultReporters();
    	Reporter[] allReporters = new Reporter[defaultReporters.length + _reporters.length + 1];
    	System.arraycopy(_reporters, 0, allReporters, 0, _reporters.length);
    	System.arraycopy(defaultReporters, 0, allReporters, _reporters.length, defaultReporters.length);
    	allReporters[allReporters.length - 1] = new TimeThresholdLoggingReporter(_jarCollection.currentJar().getName()); // new StdErrLoggingReporter();
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
			 new Melbourne(), // ok
			 new SepangMulti(), // ok
			 new Sepang(), // ok
			 new Bahrain(), // ok
			 new Imola(), // ok
			 new Barcelona(), // write???, delete :/
			 new Monaco(),
			 new Nurburgring(),
			 new Montreal(),
			 new IndianapolisFast(),
			 new IndianapolisMedium(),
			 new IndianapolisSlow(),
			 new Magnycours(), // ok
			 new Silverstone(),
			 new Hockenheim(),
			 new Hungaroring(),
			 new Istanbul(),
		};
	}

	public Driver[] drivers(Db4oEngine engine) {
		return new Driver [] {
			new MelbourneDb4o(engine),
			new SepangMultiDb4o(engine),
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
			new HockenheimDb4o(engine),
			new HungaroringDb4o(engine),
			new IstanbulDb4o(engine),
		};
	}
    
}
