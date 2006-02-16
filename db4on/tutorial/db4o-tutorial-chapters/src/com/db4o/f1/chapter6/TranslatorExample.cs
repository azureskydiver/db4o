using System;
using System.Globalization;
using com.db4o;
using com.db4o.f1;

namespace com.db4o.f1.chapter6
{
	public class TranslatorExample : Util
    {
        public static void Main(string[] args)
        {
            tryStoreWithCallConstructors();
            tryStoreWithoutCallConstructors();
            storeWithTranslator();
        }
        
        public static void tryStoreWithCallConstructors()
        {
            Db4o.configure().exceptionsOnNotStorable(true);
            Db4o.configure().objectClass(typeof(CultureInfo))
                .callConstructor(true);
            tryStoreAndRetrieve();
        }
        
        public static void tryStoreWithoutCallConstructors()
        {
            Db4o.configure().objectClass(typeof(CultureInfo))
                .callConstructor(false);
            // trying to store objects that hold onto
            // system resources can be pretty nasty
            // uncomment the following line to see
            // how nasty it can be
            //tryStoreAndRetrieve();
        }
        
        public static void storeWithTranslator()
        {
            Db4o.configure().objectClass(typeof(CultureInfo))
                .translate(new CultureInfoTranslator());
            tryStoreAndRetrieve();
            Db4o.configure().objectClass(typeof(CultureInfo))
                .translate(null);
        }
        
        public static void tryStoreAndRetrieve()
        {
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                string[] champs = new string[] { "Ayrton Senna", "Nelson Piquet" };
                LocalizedItemList LocalizedItemList = new LocalizedItemList(CultureInfo.CreateSpecificCulture("pt-BR"), champs);
                System.Console.WriteLine("ORIGINAL: {0}", LocalizedItemList);
                db.set(LocalizedItemList);
            }
            catch (Exception x)
            {
                System.Console.WriteLine(x);
                return;
            }
            finally
            {
                db.close();
            }
            db = Db4o.openFile(Util.YapFileName);
            try
            {
                ObjectSet result = db.get(typeof(LocalizedItemList));
                while (result.hasNext())
                {
                    LocalizedItemList LocalizedItemList = (LocalizedItemList)result.next();
                    System.Console.WriteLine("RETRIEVED: {0}", LocalizedItemList);
                    db.delete(LocalizedItemList);
                }
            }
            finally
            {
                db.close();
            }
        }
    }
}