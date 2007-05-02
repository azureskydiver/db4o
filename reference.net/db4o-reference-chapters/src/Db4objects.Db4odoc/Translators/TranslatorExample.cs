/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Globalization;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Translators
{
	public class TranslatorExample 
    {
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            TryStoreWithCallConstructors();
            TryStoreWithoutCallConstructors();
            StoreWithTranslator();
        }
		// end Main
        
        public static void TryStoreWithCallConstructors()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(CultureInfo))
                .CallConstructor(true);
            TryStoreAndRetrieve(configuration);
        }
		// end TryStoreWithCallConstructors
        
        public static void TryStoreWithoutCallConstructors()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(CultureInfo))
                .CallConstructor(false);
            // trying to store objects that hold onto
            // system resources can be pretty nasty
            // uncomment the following line to see
            // how nasty it can be
            //TryStoreAndRetrieve(configuration);
        }
		// end TryStoreWithoutCallConstructors
        
        public static void StoreWithTranslator()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(CultureInfo))
                .Translate(new CultureInfoTranslator());
            TryStoreAndRetrieve(configuration);
        }
		// end StoreWithTranslator
        
        public static void TryStoreAndRetrieve(IConfiguration configuration)
        {
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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
            db = Db4oFactory.OpenFile(Db4oFileName);
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