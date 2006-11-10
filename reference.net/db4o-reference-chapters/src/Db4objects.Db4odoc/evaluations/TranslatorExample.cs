using System;
using System.Globalization;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Evaluations
{
	public class TranslatorExample 
    {
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            TryStoreWithCallConstructors();
            TryStoreWithoutCallConstructors();
            StoreWithTranslator();
        }
		// end Main
        
        public static void TryStoreWithCallConstructors()
        {
            Db4oFactory.Configure().ExceptionsOnNotStorable(true);
            Db4oFactory.Configure().ObjectClass(typeof(CultureInfo))
                .CallConstructor(true);
            TryStoreAndRetrieve();
        }
		// end TryStoreWithCallConstructors
        
        public static void TryStoreWithoutCallConstructors()
        {
            Db4oFactory.Configure().ObjectClass(typeof(CultureInfo))
                .CallConstructor(false);
            // trying to store objects that hold onto
            // system resources can be pretty nasty
            // uncomment the following line to see
            // how nasty it can be
            //TryStoreAndRetrieve();
        }
		// end TryStoreWithoutCallConstructors
        
        public static void StoreWithTranslator()
        {
            Db4oFactory.Configure().ObjectClass(typeof(CultureInfo))
                .Translate(new CultureInfoTranslator());
            TryStoreAndRetrieve();
            Db4oFactory.Configure().ObjectClass(typeof(CultureInfo))
                .Translate(null);
        }
		// end StoreWithTranslator
        
        public static void TryStoreAndRetrieve()
        {
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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
            db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                IObjectSet result = db.Get(typeof(LocalizedItemList));
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
		// end TryStoreAndRetrieve
    }
}