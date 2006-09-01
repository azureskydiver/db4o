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

package org.polepos.reporters;

import java.util.*;

import org.polepos.framework.*;


public abstract class GraphReporter extends Reporter{
    
    
    private Map<CircuitLap,Graph> mGraphs;
    private List<Circuit> mCircuits;
    
    
    @Override
    public void startSeason() {
    }
    
    @Override
    public boolean append() {
        return false;
    }
    
    @Override
    public String file() {
        return "F1Results.txt";
    }
    
    @Override
    public void reportTaskName(int number, String name){
        // do nothing
    }

    @Override
    public void reportTeam(Team team) {
        // do nothing
    }

    @Override
    public void reportCar(Car car) {
        // do nothing
    }
    
    @Override
    public void beginResults() {
    }
    
    @Override
    public void reportResult(Result result) {
        
        if(mGraphs == null){
            mGraphs = new HashMap<CircuitLap,Graph>();
        }
        
        if(mCircuits == null){
            mCircuits = new ArrayList <Circuit>();
        }
        
        Circuit circuit = result.getCircuit();
        
        if(! mCircuits.contains(circuit)){
            mCircuits.add(circuit);
        }
        
        CircuitLap cl = new CircuitLap(circuit, result.getLap());
        Graph graph = mGraphs.get(cl);
        if(graph == null){
            graph = new Graph(result);
            mGraphs.put(cl, graph);
        }
        graph.addResult(mTeamCar, result);
        
    }
    
    @Override
    public void endSeason() {
        if(mGraphs != null){
            System.out.println("Checking checksums for " + getClass().getName());
            for(Circuit circuit : mCircuits){
                for(Lap lap : circuit.laps()){
                    Graph graph =mGraphs.get(new CircuitLap(circuit, lap));
                    if(graph != null){
                        graph.compareCheckSums();
                        report(graph);
                    }
                }
            }
			finish();
        }
    }

	protected abstract void report(Graph graph);
	protected abstract void finish();
}
