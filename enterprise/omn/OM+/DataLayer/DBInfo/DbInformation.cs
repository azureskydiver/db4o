using System;
using System.Collections.Generic;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext ;
using OManager.DataLayer.Connection;
using OME.Logging.Common;

namespace OManager.DataLayer.Modal
{
    public class DbInformation
    {
        public Hashtable GetAllStoredClasses()
        {
            IObjectContainer objectContainer = Db4oClient.Client; ;
          
            try
            {
                IStoredClass[] storedClasses = objectContainer.Ext().StoredClasses();
                Hashtable storedClassHashtable = new Hashtable();

                foreach (IStoredClass stored in storedClasses)
                {
                    string CheckUserDefinedClasses = stored.GetName().ToString();

                    if (!(CheckUserDefinedClasses.IndexOf("Db4objects") > -1) && !(CheckUserDefinedClasses.IndexOf("com.db4o") > -1)
                           && !(CheckUserDefinedClasses.IndexOf("System") > -1) && !(CheckUserDefinedClasses.IndexOf("db4o") > -1))
                    {
                        char[] ch = new char[] { ',' };
                        int intIndexof = CheckUserDefinedClasses.LastIndexOfAny(ch);
                        if (intIndexof > 0)
                        {
                            string strValue = CheckUserDefinedClasses.Substring(0, intIndexof);
                            storedClassHashtable.Add(CheckUserDefinedClasses, strValue);
                        }
                        else
                        {
                            storedClassHashtable.Add(CheckUserDefinedClasses, CheckUserDefinedClasses);
                        }
                    }
                }
                return storedClassHashtable;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

        public Hashtable GetAllStoredClassesAssemblyWise()
        {
            IObjectContainer objectContainer = Db4oClient.Client; ;
            try
            {
                IStoredClass[] storedClasses = objectContainer.Ext().StoredClasses();
                Hashtable storedClassHashtable = new Hashtable();
                Hashtable sortedHashtable = new Hashtable();
                List<string> classListforAssembly;


                foreach (IStoredClass stored in storedClasses)
                {
                    string CheckUserDefinedClasses = stored.GetName().ToString();

                    if (!(CheckUserDefinedClasses.IndexOf("Db4objects") > -1) && !(CheckUserDefinedClasses.IndexOf("com.db4o") > -1)
                           && !(CheckUserDefinedClasses.IndexOf("System") > -1) && !(CheckUserDefinedClasses.IndexOf("db4o") > -1))
                    {
                        char[] ch = new char[] { ',' };

                        int intIndexof = CheckUserDefinedClasses.LastIndexOfAny(ch) + 1;

                        string strValue = CheckUserDefinedClasses.Substring(intIndexof, CheckUserDefinedClasses.Length - intIndexof);

                        classListforAssembly = storedClassHashtable[strValue] as List<string>;
                        if (classListforAssembly == null)
                        {
                            classListforAssembly = new List<string>();
                            classListforAssembly.Add(CheckUserDefinedClasses);
                            storedClassHashtable.Add(strValue, classListforAssembly);
                        }
                        else
                        {
                            classListforAssembly.Add(CheckUserDefinedClasses);
                            storedClassHashtable.Remove(strValue);
                            storedClassHashtable.Add(strValue, classListforAssembly);
                        }
                    }
                }

                ArrayList hashKeys = new ArrayList(storedClassHashtable.Keys);
                hashKeys.Sort();

                foreach (string key in hashKeys)
                {
                    sortedHashtable.Add(key, storedClassHashtable[key]);
                }

                return sortedHashtable;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null; 
            }
        }

        public IList<string> SortKeys(List<string> arr)
        {
            return null;
        }


        public long GetFreeSizeofDatabase()
        {
            IObjectContainer objectContainer = Db4oClient.Client; ;
            long freeSizeOfDB = 0;
            try
            {
                freeSizeOfDB = objectContainer.Ext().SystemInfo().FreespaceSize();
            }
            catch (NotImplementedException e)
            {
                freeSizeOfDB = - 1;
               
            }
            catch (Exception oEx)
            {
                freeSizeOfDB = -1;
                LoggingHelper.HandleException(oEx);
            }

            return freeSizeOfDB;
        }

        public long getObjectsize()
        {
            return 0;
        }


        public long getTotalDatabaseSize()
        {
            IObjectContainer objectContainer = Db4oClient.Client; ;
            try
            {
                return objectContainer.Ext().SystemInfo().TotalSize();
            }
            catch (NotImplementedException)
            {
                return -1;
            }
            catch (Exception oEx)
            {
                return -2;
                LoggingHelper.HandleException(oEx);
            }

        }


        public int GetNumberOfClassesinDB()
        {
            try
            {
                Hashtable countforClasses = GetAllStoredClasses();
                return countforClasses.Count;
            }
            catch (NotImplementedException)
            {
                return -1;
            }
            catch (Exception oEx)
            {
                return -2;
                LoggingHelper.HandleException(oEx);
            }


        }
    }
}
