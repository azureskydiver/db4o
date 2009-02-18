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
            IObjectContainer objectContainer = Db4oClient.Client;

        	try
            {
                IStoredClass[] storedClasses = objectContainer.Ext().StoredClasses();
                Hashtable storedClassHashtable = new Hashtable();

                foreach (IStoredClass stored in storedClasses)
                {
                    string className = stored.GetName();

					if (!ExcludeClass(className))
                    {
                        char[] ch = new char[] { ',' };
                        int intIndexof = className.LastIndexOfAny(ch);
                        if (intIndexof > 0)
                        {
                            string strValue = className.Substring(0, intIndexof);
                            storedClassHashtable.Add(className, strValue);
                        }
                        else
                        {
                            storedClassHashtable.Add(className, className);
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

    	private static bool ExcludeClass(string className)
    	{
    		Type type = Type.GetType(className);
			return type != null 
							? typeof(IInternal4).IsAssignableFrom(type) 
							: false;
    	}

    	public Hashtable GetAllStoredClassesAssemblyWise()
        {
            IObjectContainer objectContainer = Db4oClient.Client;
        	try
            {
                IStoredClass[] storedClasses = objectContainer.Ext().StoredClasses();
                Hashtable storedClassHashtable = new Hashtable();
                Hashtable sortedHashtable = new Hashtable();
                List<string> classListforAssembly;

                foreach (IStoredClass stored in storedClasses)
                {
                    string className = stored.GetName();

                    if (!ExcludeClass(className))
                    {
                        char[] ch = new char[] { ',' };

                        int intIndexof = className.LastIndexOfAny(ch) + 1;

                        string strValue = className.Substring(intIndexof, className.Length - intIndexof);

                        classListforAssembly = storedClassHashtable[strValue] as List<string>;
                        if (classListforAssembly == null)
                        {
                            classListforAssembly = new List<string>();
                            classListforAssembly.Add(className);
                            storedClassHashtable.Add(strValue, classListforAssembly);
                        }
                        else
                        {
                            classListforAssembly.Add(className);
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

    	public long GetFreeSizeofDatabase()
        {
            IObjectContainer objectContainer = Db4oClient.Client;
        	long freeSizeOfDB;
            try
            {
                freeSizeOfDB = objectContainer.Ext().SystemInfo().FreespaceSize();
            }
            catch (NotImplementedException)
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
            IObjectContainer objectContainer = Db4oClient.Client;
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
                LoggingHelper.HandleException(oEx);
				return -2;
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
				LoggingHelper.HandleException(oEx);
				return -2;
            }
        }
    }
}
