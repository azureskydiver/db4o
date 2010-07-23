package com.db4odoc.configuration.configitems;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.BigMathSupport;
import com.db4o.config.EmbeddedConfiguration;


public class ConfigurationItemsExamples {

    public static  void addBigMathSupport(){
        // #example: Add support for BigDecimal and BigInteger
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new BigMathSupport());
        // #end example

        ObjectContainer container = Db4oEmbedded.openFile(configuration, "database.db4o");
        container.close();
    }

}
