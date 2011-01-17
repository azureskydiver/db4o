package com.db4odoc.drs.vod;


import com.db4o.drs.versant.VodDatabase;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class JDOUtilities {
    static final String VERSANT_PROPERTY_FILE = "versant.properties";

    private JDOUtilities(){}

    static PersistenceManagerFactory createPersistenceFactory() {
        // #example: Opening the persistence factory
        InputStream propertySource = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(VERSANT_PROPERTY_FILE);
        if (null == propertySource) {
            throw new RuntimeException("Couldn't find resource '" + VERSANT_PROPERTY_FILE + "' in the classpath");
        }
        try {
            return JDOHelper.getPersistenceManagerFactory(propertySource);
        } finally {
            try {
                propertySource.close();
            } catch (IOException ignored) {
            }
        }
        // #end example
    }

    static void inTransaction(PersistenceManagerFactory factory, JDOTransaction txOperation){
        PersistenceManager manager = factory.getPersistenceManager();
        try{
            manager.currentTransaction().begin();
            txOperation.invoke(manager);
            manager.currentTransaction().commit();
        } finally {
            manager.close();
        }
    }


    static VodDatabase createDatabase(PersistenceManagerFactory sessionFactory){
        Properties properties = sessionFactory.getProperties();
        String connectionURL = properties.getProperty("javax.jdo.option.ConnectionURL");
        if(isEmpty(connectionURL) || notVersantDBConnection(connectionURL)){
            throw new IllegalArgumentException("Requires a valid database connection URL for VOD");
        }
        return new VodDatabase(extractName(connectionURL),properties);
    }

    private static String extractName(String connectionURL) {
        return connectionURL.substring("versant:".length()).split("@")[0];
    }

    private static boolean notVersantDBConnection(String connectionURL) {
        return !connectionURL.startsWith("versant");
    }

    private static boolean isEmpty(String connectionURL) {
        return null==connectionURL || 0==connectionURL.trim().length();
    }
}
