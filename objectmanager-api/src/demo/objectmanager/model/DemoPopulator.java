package demo.objectmanager.model;

import com.db4o.ObjectContainer;
import com.db4o.Db4o;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

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
        List<Contact> last10 = new ArrayList<Contact>();
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
            addFriends(c, last10);
            c.setNote(new Note("This note is about " + c.getName() + ". ???? ??????????? ??????????? ??? ??, ?????? ??????? ?? ???. ?? ??? ??????? ??????? ?????????. ??? ????? ???????? ????????? ??, ??? ???????? ?????????? ??. ??? ???????? ????????? ??. ??? ???????? ????????? ???????????? ??, ?? ???????? ????????? ??????????? ???.\n" +
                    "\n" +
                    "??? ?? ????? ????????, ?????? ??????? ????????? ?? ???. ???? ?????? ???????? ??? ??, ??? ?????? ???????? ??. ?? ??????? ??????????? ??????????? ???, ????? ????? ????????? ?? ???. ?? ???? ?????? ???, ?? ??? ???? ??????.\n" +
                    "\n" +
                    "????? ?????? ??????????? ??? ??, ?? ??? ???? ??????????, ?? ??? ???? ???????. ?? ??? ?????? ??????????. ??? ??????? ?????????? ?????????? ??, ?????? ??????? ???????? ?? ???, ????? ???????? ????????? ?? ???. ?? ??????? ????????? ????????? ???. ????? ???????? ?? ???, ??? ??????? ???????? ?????????? ??. ???????? ??????????? ?????????????? ?? ???, ??? ?? ??????? ??????? ?????????, ??? ???? ?????????? ??.\n" +
                    "\n" +
                    "??? ?? ???? ?????????? ???????????, ??? ?? ?????????? ????????????????, ???? ???? ?? ???. ?? ?????? ????????? ???. ????? ??????????? ?? ???, ?? ?????? ?????? ???, ?? ?????? ????????? ???. ???? ????? ?????? ??? ??. ??? ????????? ?????????? ?????????? ??.\n" +
                    "\n" +
                    "?? ??? ???? ??????????, ??? ?? ????? ????????? ??????????. ?? ??? ????????? ????????????, ????? ???????? ?? ???. ?? ?????? ??????? ??????????? ???. ??? ???? ?????? ???????????? ??, ??? ?? ????????? ??????????.\n" +
                    "\n" +
                    "???? ????? ?? ???, ??? ????? ?????????? ??????????? ??. ?? ??? ???????? ????????? ??????????, ??? ???? ????? ?????????? ??. ??????? ?????????? ??????????? ?? ???, ?? ??????? ???????? ???, ?? ????????? ????????? ??????????????? ???. ????? ?????? ????????? ?? ???, ?? ???? ???? ???, ????? ????????? ??????????? ??? ??. ??? ?? ????? ???????.\n" +
                    "\n" +
                    "?? ??????? ???????? ?????????????? ???. ???? ?????? ???????? ?? ???, ??? ?? ??????? ?????????. ?????? ??????????????? ??? ??, ?? ??? ??????? ??????? ????????, ?? ????? ????????? ???????????? ???. ?? ???? ?????????? ???. ?? ??? ???? ??????? ??????????, ?? ?????? ???????? ?????????? ???, ??? ?? ????? ???????????.\n" +
                    "\n" +
                    "??????? ???????? ?? ???, ?? ??? ????? ??????? ????????. ?? ???????? ????????? ???, ??? ??????? ??????? ???????????? ??, ?? ??? ???? ???? ?????. ?? ?????? ??????? ???, ??? ?? ???? ??????? ?????????. ???? ????????? ?? ???, ?? ??? ????? ?????? ????????, ??????? ?????????? ??? ??.\n" +
                    "\n" +
                    "??? ?????????? ??????????? ??????????? ??. ??? ?????? ???????? ??, ?????? ???????? ?? ???. ??? ???????? ?????????? ??????????? ??, ??? ?? ??????? ????????, ?? ??? ???? ????????? ?????????. ?? ?????? ???????? ????????? ???. ????? ????? ??????? ?? ???, ?? ?????? ????????? ????????? ???, ?? ?????? ?????????? ???????????? ???.\n" +
                    "\n" +
                    "??? ????? ?????????? ??. ??? ????? ??????????? ??, ?? ??? ??????????? ???????????, ??? ?????? ??????? ?????????? ??. ???? ????? ?????? ?? ???. ??? ????? ?????????? ??. ?? ??? ???????? ????????? ??????????????.\n" +
                    "      ", false));

            db.set(c);
            last10.add(0, c);
            if(last10.size() > 10){
                last10.remove(10);
            }
        }
        db.commit();
        db.close();
    }

    private void addFriends(Contact c, List<Contact> last10) {
        c.setFriends(last10);
    }

    public String getDataFile() {
        return DB_FILE;
    }

    private synchronized ObjectContainer getDb() {
        if(db == null) {
            // delete old file if it exists
            File f = new File(DB_FILE);
            if(f.exists()) f.delete();
            //Db4o.configure().objectClass(Address.class).objectField("street").indexed(true);
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
