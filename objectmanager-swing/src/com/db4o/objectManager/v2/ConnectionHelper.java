package com.db4o.objectManager.v2;

import com.db4o.objectmanager.model.Db4oConnectionSpec;
import com.db4o.objectmanager.model.Db4oFileConnectionSpec;
import com.db4o.objectmanager.model.Db4oSocketConnectionSpec;
import com.db4o.ObjectContainer;
import com.db4o.Db4o;
import com.db4o.objectManager.v2.uiHelper.OptionPaneHelper;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oException;

import java.io.IOException;
import java.awt.Component;

/**
 * User: treeder
 * Date: Sep 17, 2006
 * Time: 10:42:31 PM
 */
public class ConnectionHelper {
    public static ObjectContainer connect(Component frame, Db4oConnectionSpec connectionSpec) throws IOException {
        if(connectionSpec instanceof Db4oFileConnectionSpec){
            try {
                return Db4o.openFile(connectionSpec.getPath());
            } catch (DatabaseFileLockedException e) {
                OptionPaneHelper.showErrorMessage(frame, "Database file is locked. Another process must be using it.", "Database File Locked");
            } catch (Db4oException e){
                // todo: finish this up after http://tracker.db4o.com/jira/browse/COR-234 is fixed
                if(e.getMessage().contains("Old database file format detected")){ // this is bad, would be nice to have a more concrete exception
                    OptionPaneHelper.showConfirmWarning(frame, "Old database file format detected. Would you like to upgrade?\n" +
                            "WARNING: This operation is irreversible and your application may not operate unless you update your db4o jar file to the latest version.", "Upgrade Database?");
                } else {
                    throw e;
                }
            }
        } else if(connectionSpec instanceof Db4oSocketConnectionSpec){
            Db4oSocketConnectionSpec spec = (Db4oSocketConnectionSpec) connectionSpec;
            return Db4o.openClient(spec.getHost(), spec.getPort(), spec.getUser(), spec.getPassword());
        }
        return null;
    }
}
