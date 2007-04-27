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


package org.polepos.db4o;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import org.polepos.*;
import org.polepos.framework.*;
import org.polepos.teams.db4o.*;


public abstract class AbstractDb4oVersionsRaceRunner extends AbstractRunner {
	
	public abstract Driver[] drivers();
	
	private String _workspace;
	
	public Team db4oTeam(String jarName, int[] options) {
    	return db4oTeam(workspace(), jarName, options, drivers()) ;
    }
    
	public Team db4oTeam(String jarName, int[] options, Driver[] drivers) {
    	return db4oTeam(workspace(), jarName, options, drivers) ;
    }
	
    private Team db4oTeam(String workspace, String jarName, int[] options, Driver[] drivers) {
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

	private void guessWorkSpace() {
        File absoluteFile = new File(new File("lib").getAbsolutePath());
        _workspace = absoluteFile.getParentFile().getParentFile().getAbsolutePath();
        System.out.println("Guessed workspace:\n" + _workspace + "\n");
    }
    
    private String workspace() {
    	if(_workspace == null) {
    		guessWorkSpace();
    	}
    	return _workspace;
    }
    
    private static void invoke(Object onObject, String methodName, Object param) throws Exception{
        Class clazz = onObject.getClass();
        Method method = clazz.getMethod(methodName, param.getClass());
        method.invoke(onObject, new Object[]{param});
    }

}
