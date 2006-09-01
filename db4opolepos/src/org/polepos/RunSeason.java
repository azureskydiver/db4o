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

import java.util.*;

import org.polepos.framework.*;


/**
 * @author Herkules
 * 
 * This is the Main class to run PolePosition.
 * If JDO is to be tested also, JdoEnhance has to be run first.
 */
public class RunSeason {
    
    /**
     * default: all Teams with all Circuits
     * @param circuit names and team names
     */
    public static void main( String[] args ){
        
        if(args == null || args.length == 0){
            new Racer(Circuits.all(), Teams.all()).run();
            return;
        }
        
        List <Circuit> circuits = new ArrayList <Circuit>();
        List <Team> teams = new ArrayList <Team>();
        for (String arg: args){
            String argLowerCase = arg.toLowerCase();
            for(Team team : Teams.all()){
                if(team.name().toLowerCase().equals(argLowerCase)){
                    teams.add(team);
                }
            }
            for(Circuit circuit: Circuits.all()){
                if(circuit.name().toLowerCase().equals(argLowerCase)){
                    circuits.add(circuit);
                }
            }
        }
        new Racer(circuits, teams).run();
    }

}
