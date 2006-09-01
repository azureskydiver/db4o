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

package org.polepos.teams.jdbc;

import java.sql.*;
import java.util.*;

import org.polepos.framework.*;


/**
 * @author Herkules
 */
public class JdbcCar extends Car{
    
    private static Connection mConnection;
    private static Statement mStatement;
    
    private final String mDBType;
    private String mName;
    
    private final static Map<Class,String> mColTypesMap = new HashMap<Class,String>();
    
    static{
        mColTypesMap.put( String.class, "VARCHAR(100)" );
        mColTypesMap.put( Integer.TYPE, "INTEGER" );
    }
    
    public JdbcCar(  String dbtype ) throws CarMotorFailureException {
        
        mDBType = dbtype;
        mWebsite = Jdbc.settings().getWebsite(mDBType);
        mDescription = Jdbc.settings().getDescription(mDBType);
        mName = Jdbc.settings().getName(mDBType);
        
        try{
            Class.forName( Jdbc.settings().getDriverClass( mDBType )).newInstance();
        }catch(Exception e){
            e.printStackTrace();
            throw new CarMotorFailureException();
        }
    }   

    public String name()
    {
        if(mName != null){
            return mName;
        }
        return mDBType;
    }

    
    public void openConnection() throws CarMotorFailureException
    {
        
        try {
            assert null == mConnection : "database has to be closed before opening";
            mConnection = DriverManager.getConnection( Jdbc.settings().getConnectUrl( mDBType ),
                        Jdbc.settings().getUsername( mDBType ), Jdbc.settings().getPassword( mDBType ) );
            mConnection.setAutoCommit( false );
            mStatement = mConnection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CarMotorFailureException();
        }
    }

    
    /**
     *
     */
    public void closeConnection()
    {
        
        if(mStatement != null){
            try {
                mStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        try
        {
            mConnection.commit();
            mConnection.close();
        }
        catch ( SQLException sqlex )
        {
            sqlex.printStackTrace();
        }
        mConnection = null;
    }
    
    
    /**
     * Commit changes.
     */
    public void commit()
    {
        try
        {
            mConnection.commit();
        }
        catch ( SQLException ex )
        {
            ex.printStackTrace();
        }        
    }


    /**
     * Declarative statement executor
     */
    public void executeSQL( String sql )
    {
//      Log.logger.fine( sql );

        Statement stmt = null;
        try
        {
            stmt = mConnection.createStatement();
            stmt.execute( sql );
        }
        catch ( SQLException ex )
        {
            ex.printStackTrace();
        }        
        finally
        {
            if (stmt != null)
            {
                try { stmt.close(); } catch (SQLException sqlEx) { stmt = null; }
            }
        }
    }    
    
    
    /**
     * Declarative statement executor
     */
    public ResultSet executeQuery( String sql )
    {
        Log.logger.fine( sql );

        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = mConnection.createStatement();
            rs = stmt.executeQuery( sql );
        }
        catch ( SQLException ex )
        {
            ex.printStackTrace();
        }        
        return rs;
    }    


    /**
     * Declarative statement executor
     */
    public ResultSet executeQueryForUpdate( String sql )
    {
        Log.logger.fine( sql );

        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = mConnection.createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
            rs = stmt.executeQuery( sql );
        }
        catch ( SQLException ex )
        {
            ex.printStackTrace();
        }        
        return rs;
    }
    
    public void executeUpdate(String sql){
        
        Log.logger.fine( sql );
        
        try {
            mStatement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Drop a certain table.
     */
    public void dropTable( String tablename )
    {
        Statement stmt = null;
        try
        {
            stmt = mConnection.createStatement();
            stmt.execute( "drop table " + tablename );
        }
        catch ( SQLException ex )
        {
            // intentionally empty
            // don't bother about 'table does not exist'
        }        
        finally
        {
            if (stmt != null)
            {
                try { stmt.close(); } catch (SQLException sqlEx) { stmt = null; }
            }
        }
    }


    /**
     * Create a new table, use the first column name as the primary key
     */
    public void createTable( String tablename, String[] colnames, Class[] coltypes )
    {
        String sql = "create table " + tablename
                + " (" + colnames[0] + "  INTEGER NOT NULL"; 


        for ( int i = 1; i < colnames.length; i++ )
        {
            sql += ", " + colnames[i] + " " + mColTypesMap.get( coltypes[i] );
        }
        sql += ", PRIMARY KEY(" + colnames[0] + "))";

        executeSQL( sql );
    }

    public void createIndex( String tablename, String colname )
    {
        // The maximum length for index names is 18 for Derby.        
        String sql = "CREATE INDEX X" + tablename + "_" + colname + " ON " + tablename + " (" + colname + ")";
        executeSQL( sql );
    }


    /**
     * Retrieve a prepared statement.
     */
    public PreparedStatement prepareStatement( String sql )
    {
        PreparedStatement stmt = null;
        try
        {
            stmt = mConnection.prepareStatement( sql );
        }
        catch ( SQLException ex )
        {
            ex.printStackTrace();
        }        
        return stmt;
    }


}
