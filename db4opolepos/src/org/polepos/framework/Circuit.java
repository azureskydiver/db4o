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

package org.polepos.framework;

import java.lang.reflect.*;
import java.util.*;

/**
 * a set of timed test cases that work against the same data
 */
public abstract class Circuit{
    
    private final List<Lap> mLaps;
    
    private final TurnSetup[] mLapSetups;
    
    private final StopWatch watch;
    
    protected Circuit(){
        watch = new StopWatch();
        mLaps = new ArrayList<Lap>();
        mLapSetups = TurnSetup.read(this);
        addLaps();
    }
    
	/**
     * public official name for reporting
	 */
    public final String name(){
        String name = internalName();
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    /**
     * internal name for BenchmarkSettings.properties
     */
    public final String internalName(){
        String name = this.getClass().getName();
        int pos = name.lastIndexOf(".");
        return name.substring(pos + 1).toLowerCase();
    }
    
    /**
     * describes the intent of this circuit, what it wants to test
     */
	public abstract String description();

    /**
     * @return the driver class needed to run on this Circuit
     */
    public abstract Class requiredDriver();
    
    /**
     * @return the methods that are intended to be run 
     */
    protected abstract void addLaps();
    
    public void add(Lap lap){
        mLaps.add(lap);
    }
    
    /**
     * setups are needed for reporting
     */
    public TurnSetup[] lapSetups(){
        return mLapSetups;
    }
    
    public List<Lap> laps() {
        return Collections.unmodifiableList(mLaps);
    }
    
    /**
     * calling all the laps for all the lapSetups
     */
    public TurnResult[] race( Team team, Car car, Driver driver){
        
        TurnResult[] results = new TurnResult[ mLapSetups.length ];

        int index = 0;
        
        for(TurnSetup setup : mLapSetups) {
            
            TurnResult result = new TurnResult(); 
            results[index++] = result;
            
            try {
                driver.takeSeatIn(car, setup);
            } catch (CarMotorFailureException e1) {
                e1.printStackTrace();
                break;
            }
            
            
            boolean first = true;
            
            for(Lap lap : mLaps) {
                
                
                Method method = null; 
            
                try {
                    method = driver.getClass().getDeclaredMethod(lap.name(), (Class[])null);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                
                
                if( ! lap.hot() ){
                    if(first){
                       first = false;
                    }else{
                        driver.backToPit();
                    }
                    
                    try {
                        driver.prepare();
                    } catch (CarMotorFailureException e) {
                        e.printStackTrace();
                    }        
                }
                
                
                watch.start();
                
                try {
                    method.invoke(driver, (Object[])null);
                } catch (Exception e) {
                    System.err.println("Exception on calling method " + method);
                    e.printStackTrace();
                }
                
                watch.stop();
                
                if(lap.reportResult()){
                    result.report(new Result(this, team, lap, setup, index, watch.millisEllapsed(), driver.checkSum(), watch.startMemory(), watch.stopMemory()));
                }
            }
            
            driver.backToPit();
        }
        return results;
    }
    
}