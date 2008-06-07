using System;
using System.Threading;
using System.Security.Principal;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Config ;
using OManager.BusinessLayer.Login;
using System.IO;

using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Connection
{
    
        public class Db4oClient
        {
            private static IObjectContainer objContainer;
            private static IObjectContainer objContainerRecentConn;
            public static ConnParams conn;
            public static string RecentConnFile;
            public static string exceptionConnection = "";
            //public static bool boolException=false;
            public static bool boolExceptionForRecentConn = false;

            private static RecentQueries currentRecentConnection;
            public static RecentQueries CurrentRecentConnection
            {
                get
                {
                    return currentRecentConnection;
                }
                set
                {
                    currentRecentConnection = value;
                }


            }

            
            /// <summary>
            /// Static property which either returns a new object container for a specific logon identity or returns the object container already 
            /// allocated to the logon identity.
            /// </summary>
            public static IObjectContainer Client
            {
                get
                {
                    exceptionConnection = "";
                    Db4oFactory.Configure().Queries().EvaluationMode(QueryEvaluationMode.LAZY);
                    //Db4oFactory.Configure().UpdateDepth(int.MaxValue);
                    Db4oFactory.Configure().ActivationDepth(1);
                    Db4oFactory.Configure().AllowVersionUpdates(true);
                    Db4oFactory.Configure().BlockSize(8);   
                    // Retrieve the object container for the current identity.
                   // LocalDataStoreSlot slot = Thread.GetNamedDataSlot(WindowsIdentity.GetCurrent().Name);
                    //IObjectContainer objectContainer = (IObjectContainer)Thread.GetData(slot);

                    try
                    {
                        if (objContainer == null)
                        {
                            // Prior to opening the objectContainer set all required Db4o configurations.
                            // Db4oConfiguration.SetConfiguration();
                            if (conn != null)
                            {
                                // Retrieve an objectContainer for this client. 
                                if (conn.Host != null)
                                {

                                    objContainer = Db4oFactory.OpenClient(conn.Host, (int)conn.Port,
                                                      conn.UserName, conn.PassWord);
                                }
                                else
                                {
                                    if (File.Exists(conn.Connection))
                                    {


                                        objContainer = Db4oFactory.OpenFile(conn.Connection);
                                    }
                                    else
                                    {
                                        exceptionConnection = "File does not exist!";
                                    }
                                }

                                // Store the allocated object container for this current logon identity.  
                               // Thread.SetData(slot, objectContainer);
                            }
                        }
                       
                    }
                    catch (InvalidPasswordException)
                    {
                        exceptionConnection = "Incorrect Credentials. Please enter again.";
                    }
                    catch (DatabaseFileLockedException)
                    {
                        exceptionConnection = "Database is locked and is used by another application.";
                    }
                    //catch (Db4objects.Db4o.Ext.Db4oException ex)
                    //{
                    //    //File format incompatible.
                    //    exceptionConnection = ex.Message;
                    //}
                    catch (Db4objects.Db4o.IncompatibleFileFormatException ex)
                    {
                        exceptionConnection = ex.Message;
                    }
                    catch (System.Net.Sockets.SocketException)
                    {
                        exceptionConnection = "No connection could be made because the target machine actively refused it.";
                    }
                    catch (System.InvalidCastException)
                    {
                        exceptionConnection = "Java Database is not supproted.";
                    }
                   
                    catch (Exception oEx)
                    {
                        exceptionConnection = oEx.Message;
                        //return null;
                    }
                    return objContainer;
                }

            }

            public static IObjectContainer RecentConn
            {
                get
                {

                   // LocalDataStoreSlot slot = Thread.GetNamedDataSlot("RecentConnections");
                  //  IObjectContainer objectContainer = (IObjectContainer)Thread.GetData(slot);
                    //IObjectContainer objectContainer = null;

                    try
                    {
                        if (objContainerRecentConn == null && RecentConnFile != null)
                        {
                            //Db4oFactory.Configure().ObjectClass(typeof(RecentQueries)).UpdateDepth(10);
                            //Db4oFactory.Configure().ObjectClass(typeof(RecentQueries)).MinimumActivationDepth(10); 
                            Db4oFactory.Configure().UpdateDepth(int.MaxValue);
                            Db4oFactory.Configure().ActivationDepth(int.MaxValue);
                            Db4oFactory.Configure().LockDatabaseFile(false);
                            objContainerRecentConn = Db4oFactory.OpenFile(RecentConnFile);                            
                            //Thread.SetData(slot, objectContainer);
                        }
                    }
                    catch (Exception oEx)
                    {
                        LoggingHelper.HandleException(oEx);
                        boolExceptionForRecentConn = true;
                    }

                    return objContainerRecentConn;
                }
            }


            /// <summary>
            /// Static property which closes the corresponding object container for the current logon identity.
            /// </summary>
            public static void CloseConnection(IObjectContainer objectContainer)
            {
                //LocalDataStoreSlot slot = Thread.GetNamedDataSlot(WindowsIdentity.GetCurrent().Name);
                objectContainer = Client;

                try
                {
                    if (objectContainer != null)
                    {
                        objectContainer.Close();
                        objectContainer = null;
                        objContainer = null;
                    }
                    conn = null;
                   
                }
                catch (Exception oEx)
                {
                    LoggingHelper.HandleException(oEx);
                    
                }
            }

            public static void CloseRecentConnectionFile(IObjectContainer objectContainer)
            {
               // LocalDataStoreSlot slot = Thread.GetNamedDataSlot("RecentConnections");
                objectContainer = RecentConn;

                try
                {
                    if (objectContainer != null)
                    {
                        objectContainer.Close();
                        objectContainer = null;
                        objContainerRecentConn = null;
                      //  Thread.SetData(slot, objectContainer);

                    }

                  
                }
                catch (Exception oEx)
                {
                    LoggingHelper.HandleException(oEx);
                   
                }
            }

            //public static IObjectContainer ReturnObjContainerFromSlot()
            //{
            //    LocalDataStoreSlot slot = Thread.GetNamedDataSlot(WindowsIdentity.GetCurrent().Name);
            //    IObjectContainer objectContainer = (IObjectContainer)Thread.GetData(slot);
            //}
        }
   
}
