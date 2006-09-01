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
import java.net.*;

import org.polepos.framework.*;
import org.polepos.teams.db4o.*;

/**
 * uses Poleposition to run multiple db4o versions agains 
 * eachother to test performance progress.
 */
public class Db4oVersionRace {
    
    public static void main(String[] arguments) {
        new Racer(Circuits.all(), versions()).run();
    }
    
    public static Team[] versions(){
        
        String jar45 = "db4o-4.5-java1.4.jar";
        String jar50 = "db4o-5.0-java5.jar";
        String jar52 = "db4o-5.2-java5.jar";
        String jar55 = "db4o-5.5-java5.jar";
        String jar57 = "db4o-5.7.2709-java5.jar";
        
        
        return new Team[]{
            
            db4oTeam(null, new int[]{} ),

        
//            db4oTeam(jar45, null),
//            db4oTeam(jar45, new int[]{Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP} ),
            
//            db4oTeam(jar50, null),
//            db4oTeam(jar50, new int[]{Db4oOptions.NO_FLUSH} ),
            
//            db4oTeam(jar52, new int[]{} ),
//            db4oTeam(jar52, new int[]{Db4oOptions.NO_FLUSH} ),
//            db4oTeam(jar52, new int[]{Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP} ),
//            db4oTeam(jar52, new int[]{Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP, Db4oOptions.NO_FLUSH} ),

            db4oTeam(jar55, new int[]{} ),
            db4oTeam(jar57, new int[]{} ),

//            db4oTeam(jar55, new int[]{Db4oOptions.CACHED_BTREE_ROOT} ),
//            db4oTeam(jar55, new int[]{Db4oOptions.NO_FLUSH} ),
//            db4oTeam(jar55, new int[]{Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP} ),
//            db4oTeam(jar55, new int[]{Db4oOptions.CLIENT_SERVER, Db4oOptions.CLIENT_SERVER_TCP, Db4oOptions.NO_FLUSH} ),
        };
    }
    
    private static Team db4oTeam(String jarName, int[] options) {
        try {
            
            Team team = null;
            
            if(jarName == null){
                team = (Team)Class.forName(Db4oTeam.class.getName()).newInstance();
            }else{
                String[] prefixes={"com.db4o.","org.polepos.teams.db4o."};
                URL classURL=new File("bin").toURL();
                URL jarURL=new File("lib/"+jarName).toURL();
                
                // System.out.println(classURL+" , "+jarURL);
                
                ClassLoader loader=new VersionClassLoader(new URL[]{classURL,jarURL},prefixes);
                team = (Team)loader.loadClass(Db4oTeam.class.getName()).newInstance();
            }
            team.configure(options);
            return team;
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
    

}
