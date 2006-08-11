using System;
using System.Globalization;
using com.db4o;
using com.db4odoc.f1;

namespace com.db4odoc.f1.evaluations
{
	public class TranslatorExample : Util
    {
        public static void Main(string[] args)
        {
            TryStoreWithCallConstructors();
            TryStoreWithoutCallConstructors();
            StoreWithTranslator();
        }
        
        public static void TryStoreWithCallConstructors()
        {
            Db4o.Configure().ExceptionsOnNotStorable(true);
            Db4o.Configure().ObjectClass(typeof(CultureInfo))
                .CallConstructor(true);
            TryStoreAndRetrieve();
        }
        
        public static void TryStoreWithoutCallConstructors()
        {
            Db4o.Configure().ObjectClass(typeof(CultureInfo))
                .CallConstructor(false);
            // trying to store objects that hold onto
            // system resources can be pretty nasty
            // uncomment the following line to see
            // how nasty it can be
            //TryStoreAndRetrieve();
        }
        
        public static void StoreWithTranslator()
        {
            Db4o.Configure().ObjectClass(typeof(CultureInfo))
                .Translate(new CultureInfoTranslator());
            TryStoreAndRetrieve();
            Db4o.Configure().ObjectClass(typeof(CultureInfo))
                .Translate(null);
        }
        
        public static void TryStoreAndRetrieve()
        {
            ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
            try
            {
                string[] champs = new string[] { "Ayrton Senna", "Nelson Piquet" };
                LocalizedItemList LocalizedItemList = new LocalizedItemList(CultureInfo.CreateSpecificCulture("pt-BR"), champs);
                System.Console.WriteLine("ORIGINAL: {0}", LocalizedItemList);
                db.Set(LocalizedItemList);
            }
            catch (Exception x)
            {
                System.Console.WriteLine(x);
                return;
            }
            finally
            {
                db.Close();
            }
            db = Db4o.OpenFile(Util.YapFileName);
            try
            {
                ObjectSet result = db.Get(typeof(LocalizedItemList));
                while (result.HasNext())
                {
                    LocalizedItemList LocalizedItemList = (LocalizedItemList)result.Next();
                    System.Console.WriteLine("RETRIEVED: {0}", LocalizedItemList);
                    db.Delete(LocalizedItemList);
                }
            }
            finally
            {
                db.Close();
            }
        }
    }
}