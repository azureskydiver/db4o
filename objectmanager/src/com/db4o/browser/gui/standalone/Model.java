/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.gui.standalone;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;

/**
 * Class Model.
 * 
 * @author djo
 */
public class Model {
	private static ObjectContainer container = null;
    
    public static void open() {
        container=Db4o.openFile("formula1.yap");
    }
    
    public static StoredClass[] storedClasses() {
       	return container.ext().storedClasses();
    }
    
    public static ObjectSet instances(String clazz) {
        Query q = container.query();
        Class toReturn = null;
        try {
        	toReturn = Class.forName(clazz);
        }
        catch (Exception e) {
            // RIXME: Log this later...
        }
        return container.get(toReturn);
    }
    
    public static void close() {
        container.close();
    }
    
}
