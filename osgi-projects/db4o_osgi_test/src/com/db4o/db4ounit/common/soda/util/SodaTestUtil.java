/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.common.soda.util;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;

public class SodaTestUtil {

    public static void expectOne(Query query, Object object) {
        expect(query, new Object[] { object });
    }

    public static void expectNone(Query query) {
        expect(query, null);
    }

    public static void expect(Query query, Object[] results) {
        expect(query, results, false);
    }

    public static void expectOrdered(Query query, Object[] results) {
        expect(query, results, true);
    }

    public static void expect(Query query, Object[] results, boolean ordered) {
        ObjectSet set = query.execute();
        if (results == null || results.length == 0) {
            if (set.size() > 0) {
                Assert.fail("No content expected.");
            }
            return;
        }
        int j = 0;
        if (set.size() == results.length) {
            while (set.hasNext()) {
                Object obj = set.next();
                boolean found = false;
                if (ordered) {
                    if (TCompare.isEqual(results[j], obj)) {
                        results[j] = null;
                        found = true;
                    }
                    j++;
                } else {
                    for (int i = 0; i < results.length; i++) {
                        if (results[i] != null) {
                            if (TCompare.isEqual(results[i], obj)) {
                                results[i] = null;
                                found = true;
                                break;
                            }
                        }
                    }
                }
                if (!found) {
                    Assert.fail("Object not expected: " + obj);
                }
            }
            for (int i = 0; i < results.length; i++) {
                if (results[i] != null) {
                    Assert.fail("Expected object not returned: " + results[i]);
                }
            }
        } else {
            Assert.fail(
                "Unexpected size returned.\nExpected: "
                    + results.length
                    + " Returned: "
                    + set.size());
        }
    }
	
	private SodaTestUtil() {}
}
