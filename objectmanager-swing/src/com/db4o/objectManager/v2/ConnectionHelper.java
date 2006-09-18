package com.db4o.objectManager.v2;

import com.db4o.objectmanager.model.Db4oConnectionSpec;
import com.db4o.objectmanager.model.Db4oFileConnectionSpec;
import com.db4o.objectmanager.model.Db4oSocketConnectionSpec;
import com.db4o.ObjectContainer;
import com.db4o.Db4o;

import java.io.IOException;

/**
 * User: treeder
 * Date: Sep 17, 2006
 * Time: 10:42:31 PM
 */
public class ConnectionHelper {
    public static ObjectContainer connect(Db4oConnectionSpec connectionSpec) throws IOException {
        if(connectionSpec instanceof Db4oFileConnectionSpec){
            return Db4o.openFile(connectionSpec.getPath());
        } else if(connectionSpec instanceof Db4oSocketConnectionSpec){
            Db4oSocketConnectionSpec spec = (Db4oSocketConnectionSpec) connectionSpec;
            return Db4o.openClient(spec.getHost(), spec.getPort(), spec.getUser(), spec.getPassword());
        }
        return null;
    }
}
