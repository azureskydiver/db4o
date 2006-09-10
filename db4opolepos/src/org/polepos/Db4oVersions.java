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
import java.lang.reflect.*;
import java.net.*;

import org.polepos.framework.*;
import org.polepos.teams.db4o.*;
import org.polepos.teams.hibernate.*;
import org.polepos.teams.jdbc.*;


public class Db4oVersions {
    
    private static String guessWorkSpace() {
        File absoluteFile = new File(new File("lib").getAbsolutePath());
        String workspace = absoluteFile.getParentFile().getParentFile().getAbsolutePath();
        System.out.println("Guessed workspace:\n" + workspace + "\n");
        return workspace;
    }
    
    public static Team[] teams(){
        
        String workSpacePath = guessWorkSpace();
        
        String jar45 = "db4o-4.5-java1.4.jar";
        String jar50 = "db4o-5.0-java5.jar";
        String jar52 = "db4o-5.2-java5.jar";
        String jar55 = "db4o-5.5-java5.jar";

        
        return new Team[]{
            workspaceTeam(workSpacePath),
            db4oTeam(workSpacePath, jar45, new int[]{} ),
            db4oTeam(workSpacePath, jar50, new int[]{} ),
            db4oTeam(workSpacePath, jar52, new int[]{} ),
            db4oTeam(workSpacePath, jar55, new int[]{} ),
            new HibernateTeam(),
            new JdbcTeam(),
        };
    }
    
    private static Team workspaceTeam(String workspace){
        return db4oTeam(workspace, null, new int[]{} );
    }
    
    private static Team db4oTeam(String workspace, String jarName, int[] options) {
        
        try {
            Team team = null;
            
            if(jarName == null){
                team = (Team)Class.forName(Db4oTeam.class.getName()).newInstance();
            }else{
                String[] prefixes={"com.db4o.","org.polepos.teams.db4o."};
                
                URL classURL=new File("bin").toURL();
                URL jarURL=new File("lib/"+jarName).toURL();
                
                URL poleposClassURL=new File(workspace + "/polepos/bin").toURL();
                
                // System.out.println(classURL+" , "+jarURL + " , " + poleposClassURL);
                
                ClassLoader loader=new VersionClassLoader(new URL[]{poleposClassURL, classURL, jarURL},prefixes);
                team = (Team)loader.loadClass(Db4oTeam.class.getName()).newInstance();
                
            }
            team.configure(options);
            
            Object[] drivers = NewDrivers.drivers();
            
            for (int i = 0; i < drivers.length; i++) {
                String driverName = drivers[i].getClass().getName();
                invoke(team, "addDriver", driverName);
            }
            
            return team;
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
    
    private static void invoke(Object onObject, String methodName, Object param) throws Exception{
        Class clazz = onObject.getClass();
        Method method = clazz.getMethod(methodName, param.getClass());
        method.invoke(onObject, new Object[]{param});
    }

}
