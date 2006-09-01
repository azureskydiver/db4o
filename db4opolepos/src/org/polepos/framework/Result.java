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

/**
 * a result for a lap that holds the name and the time.
 */
public class Result {
    
    
    private final Circuit mCircuit;
    
    private final Team mTeam;
    
    private final TurnSetup mSetup;
    
    private final int mIndex;
    
    private final Lap mLap;
    
    private final long mTime;
    
    private final long mCheckSum;
    
    private final long mStartMemory;
    
    private final long mStopMemory;
    
    
    public Result(Circuit circuit, Team team, Lap lap, TurnSetup setup, int index, long time, long checkSum, long startMemory, long stopMemory){
        mCircuit = circuit;
        mTeam = team;
        mLap = lap;
        mSetup = setup;
        mIndex = index;
        mTime = time;
        mCheckSum = checkSum;
        mStartMemory = startMemory;
        mStopMemory = stopMemory;
    }
    
    public String getName(){
        return mLap.name();
    }
    
    public long getTime(){
        return mTime;
    }
    
    public TurnSetup getSetup(){
        return mSetup;
    }
    
    public int getIndex(){
        return mIndex;
    }
    
    public Circuit getCircuit(){
        return mCircuit;
    }
    
    public Lap getLap(){
        return mLap;
    }
    
    public long getCheckSum(){
        return mCheckSum;
    }
    
    public Team getTeam(){
        return mTeam;
    }
    
    public long getMemoryIncrease(){
        return mStopMemory - mStartMemory;
    }
    
}

