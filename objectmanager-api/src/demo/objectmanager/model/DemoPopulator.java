package demo.objectmanager.model;

import com.db4o.ObjectContainer;
import com.db4o.Db4o;

import java.io.File;

/**
 * User: treeder
 * Date: Aug 30, 2006
 * Time: 11:59:28 AM
 */
public class DemoPopulator {
    private ObjectContainer db;
    private static final String DB_FILE = "demo.db";
    private static final int NUMBER_TO_MAKE = 5000;

    public static void main(String[] args) {
        DemoPopulator p = new DemoPopulator();
        p.start();
    }

    public void start() {
        ObjectContainer db = getDb();
        int ageCounter = 0;
        for(int i = 0; i < NUMBER_TO_MAKE; i++){
            Contact c = new Contact();
            c.setId(new Integer(i));
            c.setName("Contact " + i);
            c.setAge(++ageCounter);
            if(ageCounter >= 100){
                ageCounter = 0;
            }
            addAddresses(c);
            addEmails(c);
            db.set(c);
        }
        db.commit();
        db.close();
    }
    public String getDataFile() {
        return DB_FILE;
    }

    private synchronized ObjectContainer getDb() {
        if(db == null) {
            // delete old file if it exists
            File f = new File(DB_FILE);
            if(f.exists()) f.delete();
            db = Db4o.openFile(DB_FILE);
        }
        return db;
    }

    private void addEmails(Contact c) {
        for(int i = 0; i < 10; i++){
            c.addEmail(new EmailAddress("name@somewhere" + i + ".com"));
        }
    }

    private void addAddresses(Contact c) {
        for(int i = 0; i < 5; i++){
            c.addAddress(new Address(c, i + " street", "San Francisco", "CA", "90210"));
        }
    }


}
