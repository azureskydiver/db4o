package com.db4o.objectmanager.api.helpers;

import com.db4o.reflect.ReflectClass;
import com.db4o.ext.StoredClass;
import com.db4o.ObjectContainer;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.text.Collator;

/**
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 7:02:15 PM
 */
public class ReflectHelper {

    /**
     *
     * @param container
     * @return List<ReflectClass>
     */
    public static List getUserStoredClasses(ObjectContainer container) {
        String[] ignore = new String[]{
            "com.db4o.P1Object",
            "j4o.lang.AssemblyNameHint, db4o",
            "java.lang.Object"
        };

        // YYY
        //System.out.println("PURGE");
        //container.ext().purge();

        // Get the known classes
        ReflectClass[] knownClasses = container.ext().knownClasses();

        // Sort them
        Arrays.sort(knownClasses, new Comparator() {
            private Collator comparator = Collator.getInstance();
            public int compare(Object arg0, Object arg1) {
                ReflectClass class0 = (ReflectClass) arg0;
                ReflectClass class1 = (ReflectClass) arg1;

                return comparator.compare(class0.getName(), class1.getName());
            }
        });

        // Filter them
        LinkedList filteredList = new LinkedList();
        for (int i = 0; i < knownClasses.length; i++) {

            boolean take = true;
            for (int j = 0; j < ignore.length; j++) {
                if(knownClasses[i].getName().equals(ignore[j])){
                    take = false;
                    break;
                }
            }
            if(! take){
                continue;
            }

            if (knownClasses[i].isArray()) {
                continue;
            }

            StoredClass sc = container.ext().storedClass(knownClasses[i].getName());
            if(sc == null){
                continue;
            }
            filteredList.add(knownClasses[i]);
        }
        return filteredList;
    }
}
