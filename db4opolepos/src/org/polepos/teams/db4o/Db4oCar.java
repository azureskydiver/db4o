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

package org.polepos.teams.db4o;

import java.io.*;

import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.ext.*;


public class Db4oCar extends Car {
    
    private static final int SERVER_PORT = 4488;
    
    private static final String SERVER_HOST = "localhost";
    
    private static final String SERVER_USER = "db4o";
    
    private static final String SERVER_PASSWORD = "db4o";
    
	private String name;
    
    private boolean _clientServer;
    
    private boolean _clientServerOverTcp;
    
    private ObjectServer _server;
    
    public static final String FOLDER = "data/db4o";

    private static final String DB4O_FILE = "dbbench.yap"; 

	Db4oCar(boolean clientServer, boolean clientServerOverTcp) {
        _clientServer = clientServer;
        _clientServerOverTcp = clientServerOverTcp;
        name = Db4o.version().substring(5);
	}

	@Override
	public String name() {
		return name;
	}
    
    /**
     * Initialize the database by deleting the database file.
     */
    public void initialize()
    {
        new File(FOLDER).mkdirs();
        deleteDatabaseFile();
    }
    
    /**
     * Open database in the configured mode.
     */
    public ExtObjectContainer createObjectContainer()
    {
        if(! _clientServer){
            return Db4o.openFile( path() ).ext();
        }
        
        if(_clientServer){
            Db4o.configure().messageLevel(-1);
            _server = Db4o.openServer(path(), SERVER_PORT);
            _server.grantAccess(SERVER_USER, SERVER_PASSWORD);
        }
        
        if(_clientServerOverTcp){
            try {
                return Db4o.openClient(SERVER_HOST, SERVER_PORT, SERVER_USER, SERVER_PASSWORD).ext();
            } catch (IOException e) {
                
                // Can happen if port not available
                // Check SERVER_ settings
                
                e.printStackTrace();
            }
            return null;
        }
        
        return _server.openClient().ext();
    }
    
    /**
     * closes any server if opened 
     */
    public void closeServer(){
        if(_server != null){
            _server.close();
        }
        _server = null;
    }
    
    /**
     * get rid of the database file.
     */
    private void deleteDatabaseFile()
    {
        new File( path() ).delete();
    }
    
    
    private final String path(){
        return FOLDER + "/" + DB4O_FILE;
        
    }
    

}
