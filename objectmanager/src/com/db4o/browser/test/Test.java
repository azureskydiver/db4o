/*
 * Created on Jan 24, 2005
 */
package com.db4o.browser.Test;

import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Class Test.
 *
 * @author djo
 */
public class Test {
    public static void main(String[] args) {
        ObjectContainer container=Db4o.openFile("c:\\workspace\\com.db4o.browser\\formula1.yap");
        Query q = container.query();
        container.get(List.class);
        container.close();
    }
}
