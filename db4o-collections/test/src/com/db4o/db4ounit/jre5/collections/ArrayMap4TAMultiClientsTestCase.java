package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.ArrayMap4;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ta.TransparentActivationSupport;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oClientServerFixture;
import db4ounit.extensions.fixtures.OptOutSolo;

public class ArrayMap4TAMultiClientsTestCase extends AbstractDb4oTestCase
        implements OptOutSolo {

    public static void main(String[] args) {
        new ArrayMap4TAMultiClientsTestCase().runEmbeddedClientServer();
    }
    
    protected void store() throws Exception {
        ArrayMap4<String, Integer> map = new ArrayMap4<String, Integer>();
        ArrayMap4Asserter.putData(map);
        store(map);
    }

    protected void configure(Configuration config) throws Exception {
        config.add(new TransparentActivationSupport());
        config.activationDepth(0);
        super.configure(config);
    }

    protected ExtObjectContainer openNewClient() {
        return clientServerFixture().openNewClient();
    }

    protected Db4oClientServerFixture clientServerFixture() {
        return (Db4oClientServerFixture) fixture();
    }

    private ArrayMap4<String, Integer> retrieveOnlyInstance(
            ExtObjectContainer db) {
        ArrayMap4<String, Integer> map = CollectionsUtil.retrieveMapFromDB(db,
                reflector());
        return map;
    }

    private ArrayMap4<String, Integer> retrieveOnlyInstance() {
        ArrayMap4<String, Integer> map = CollectionsUtil.retrieveMapFromDB(
                db(), reflector());
        return map;
    }

    public void testClearClear() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        map2.clear();
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Assert.areEqual(0, map.size());
    }
    
    public void testClearPut() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        map2.put("10", Integer.valueOf(10* 100));
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(11, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 11);
    }
    
    public void testClearRemove() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        map2.remove("0");
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(9, map.size());
        ArrayMap4Asserter.checkMap(map, 1, 10);
    }
    
    public void testClearGet() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();
        ExtObjectContainer client3 = openNewClient();
        ExtObjectContainer client4 = openNewClient();
        ExtObjectContainer client5 = openNewClient();
        ExtObjectContainer client6 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        ArrayMap4<String, Integer> map3 = retrieveOnlyInstance(client3);
        ArrayMap4<String, Integer> map4 = retrieveOnlyInstance(client4);
        ArrayMap4<String, Integer> map5 = retrieveOnlyInstance(client5);
        ArrayMap4<String, Integer> map6 = retrieveOnlyInstance(client6);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        ArrayMap4Asserter.assertContainsKey(map3);
        ArrayMap4Asserter.assertContainsValue(map4);
        ArrayMap4Asserter.assertEntrySet(map5);
        ArrayMap4Asserter.assertKeySet(map6);
        
        client1.set(map1);
        client2.set(map2);
        client3.set(map3);
        client4.set(map4);
        client5.set(map5);
        client6.set(map6);
        
        client1.close();
        client2.close();
        client3.close();
        client4.close();
        client5.close();
        client6.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(10, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 10);
    }
    
    @SuppressWarnings("unchecked")
    public void testClearClone() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        ArrayMap4<String, Integer> clone = (ArrayMap4<String, Integer>) map2.clone();
        client1.set(map1);
        client2.delete(map2);
        client2.set(clone);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(10, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 10);
    }
    
    public void testPutPut() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.put("11", Integer.valueOf(11 * 100));
        map2.put("10", Integer.valueOf(10 * 100));
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 11);
        Assert.isNull(map.get("11"));
    }
    
    public void testPutPutAll() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.put("100", Integer.valueOf(100 * 100));
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 50; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        map2.putAll(other);
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 50);
        Assert.isNull(map.get("100"));
    }
    
    public void testPutRemove() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.put("11", Integer.valueOf(11 * 100));
        Integer value = map2.remove("9");
        Assert.areEqual(Integer.valueOf(900), value);
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 9);
        Assert.isNull(map.get("9"));
    }
    
    public void testPutClear() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.put("11", Integer.valueOf(11 * 100));
        map2.clear();
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testPutAllClear() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 50; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        map1.putAll(other);
        map2.clear();
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testPutAllPut() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 50; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        map1.putAll(other);
        map2.put("-1", Integer.valueOf(-1 * 100));
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, -1, 10);
    }
    
    public void testPutAllPutAll() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        ArrayMap4<String, Integer> other1 = new ArrayMap4<String, Integer>();
        for (int i = 30; i < 50; i++) {
            other1.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        
        ArrayMap4<String, Integer> other2 = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 30; i++) {
            other2.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        
        map1.putAll(other1);
        map2.putAll(other2);
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 30);
    }
    
    public void testPutAllRemove() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 50; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        map1.putAll(other);
        map2.remove("9");
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 9);
    }
    
    public void testRemoveClear() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.remove("9");
        map2.clear();
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testRemovePut() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.remove("0");
        map2.put("10", Integer.valueOf(10 * 100));
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 11);
    }
    
    public void testRemovePutAll() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        Integer value = map1.remove("9");
        Assert.areEqual(Integer.valueOf(900), value);
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 50; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        map2.putAll(other);
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 50);
        Assert.isNull(map.get("100"));
    }
    
    public void testRemoveRemove() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        map1.remove("0");
        map2.remove("9");
        
        client1.set(map1);
        client2.set(map2);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 9);
    }
}
