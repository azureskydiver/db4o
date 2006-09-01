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
 * @author Herkules
 */
public class StopWatch
{
    
	private long startNanos;
    
	private long nanosEllapsed;
    
    private long startMemory;
    
    private long stopMemory; 
    

	public void start()
	{
        startMemory = usedMemory();
		startNanos = System.nanoTime();
	}
    

	public void stop()
	{
		nanosEllapsed = System.nanoTime() - startNanos;
        stopMemory = usedMemory();
	}

	public long millisEllapsed()
	{
        long res = nanosEllapsed / (long) 1000000;
        // return at least two, to get a nice logarithmic value.
        if(res < 2){
            res = 2;
        }
        return res;
	}

	public String toString()
	{
		return "" + millisEllapsed() + "ms";
	}
    
private long usedMemory(){
    Runtime rt = Runtime.getRuntime();
    while(true){
        long memory1 = rt.freeMemory();    
        System.gc();
        System.runFinalization();
        long memory2 = rt.freeMemory();
        if(memory2 >= memory1){
            break;
        }
    };
    return rt.totalMemory() - rt.freeMemory();
}
    
    public long startMemory(){
        return startMemory;
    }
    
    public long stopMemory(){
        return stopMemory;
    }
    
	
}
