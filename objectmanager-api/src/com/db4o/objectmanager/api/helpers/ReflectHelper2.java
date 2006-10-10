package com.db4o.objectmanager.api.helpers;

import com.db4o.reflect.ReflectClass;
import com.db4o.ext.StoredClass;
import com.db4o.ObjectContainer;

import java.util.*;
import java.text.Collator;

/**
 * 
 * <p>
 * Named ReflectHelper2 so it doesn't conflict with the one in db4o-sql
 * </p>
 *
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 7:02:15 PM
 */
public class ReflectHelper2 {

    /**
     * <p>
     * Returns all the classes that the user would have stored.  Ignores core java classes and db4o classes.
     * </p>
     * @param container
     * @return List<ReflectClass>
     */
    public static List getUserStoredClasses(ObjectContainer container) {
        String[] ignore = new String[]{
                // todo: decide which should be filtered for sure
                "java.lang.",
                "java.util.",
                "java.math.",
                "com.db4o.",
                "j4o.lang.AssemblyNameHint",
                "System."
        };

        // Get the known classes
        ReflectClass[] knownClasses = container.ext().knownClasses();

        // Filter them
        List filteredList = new ArrayList();
        for (int i = 0; i < knownClasses.length; i++) {
            ReflectClass knownClass = knownClasses[i];
            if (knownClass.isArray() || knownClass.isPrimitive()) {
                continue;
            }
            boolean take = true;
            for (int j = 0; j < ignore.length; j++) {
                if(knownClass.getName().startsWith(ignore[j])){
                    take = false;
                    break;
                }
            }
            if(! take){
                continue;
            }
            StoredClass sc = container.ext().storedClass(knownClass.getName());
            if(sc == null){
                continue;
            }
            filteredList.add(knownClass);
        }

          // Sort them
        Collections.sort(filteredList, new Comparator() {
            private Collator comparator = Collator.getInstance();
            public int compare(Object arg0, Object arg1) {
                ReflectClass class0 = (ReflectClass) arg0;
                ReflectClass class1 = (ReflectClass) arg1;

                return comparator.compare(class0.getName(), class1.getName());
            }
        });

        return filteredList;
    }

    /**
     * <p>
     * Equivalent to isLeaf, isEditable, isSortaPrimitive... ;)
     * </p>
     * @param c
     * @return
     */
    public static boolean isEditable(Class c) {
        return c.isPrimitive() || String.class.isAssignableFrom(c) || Number.class.isAssignableFrom(c) || Date.class.isAssignableFrom(c);
    }
}
