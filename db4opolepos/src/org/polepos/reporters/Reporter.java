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

import java.io.*;

import org.polepos.framework.*;


/**
 * base class for reporting the results.
 */
public abstract class Reporter {
    
    protected final static String WEBSITE="http://www.polepos.org";
    
    protected final static String PATH="doc/results";
        
    protected Car mCar;
    
    protected Team mTeam;
    
    protected TeamCar mTeamCar;
    
    private boolean mTaskListPrinted;
    
    public Reporter(){
        new File(PATH).mkdirs();
    }
    
    public abstract String file();
    
    public abstract boolean append();
    
    public abstract void startSeason();
    
    public abstract void endSeason();
    
    public String path(){
        return new File(PATH).getAbsolutePath();
    }

    public void sendToCircuit(Circuit circuit) {
        mTaskListPrinted = false;
    }
    
    public void noDriver(Team team, Circuit circuit) {
        // default: do nothing
    }
    
    public void report( Team team, Car car, TurnSetup[] setups, TurnResult[] results ){
        
        if(! mTaskListPrinted){
            results[0].requestTaskNames(this);
            mTaskListPrinted = true;
        }
        
        if(team != mTeam){
            mTeam = team;
            reportTeam(team);
        }
        
        if(car != mCar){
            mCar = car;
            reportCar(car);
        }
        
        mTeamCar = new TeamCar(team, car);
        
        reportSetups(setups);
        
        for ( int i = 0; i < results.length; i++ ){
            beginResults();
            if(results[i] != null){
                results[i].report(this);
            }
        }
    }
        
    public abstract void reportTaskName(int number, String name);
    
    protected abstract void reportTeam(Team team);
    
    protected abstract void reportCar(Car car);
    
    public void reportSetups(TurnSetup[] setups){
        
    }
    
    protected abstract void beginResults();

    public abstract void reportResult(Result result);

}
