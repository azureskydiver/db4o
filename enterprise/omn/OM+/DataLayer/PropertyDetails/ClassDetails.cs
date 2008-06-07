using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.BusinessLayer.QueryManager;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Login;

using OManager.DataLayer.CommonDatalayer;
using OManager.BusinessLayer.Common;

using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Modal
{
    class ClassDetails 
    {
        private IObjectContainer objectContainer;
        private string m_className;
        public  ClassDetails(string className)
        {
            this.m_className = DataLayerCommon.RemoveGFromClassName(className);
            objectContainer = Db4oClient.Client; 
        }

        public int GetNumberOfObjects()
        {
            try
            {
                              
                return objectContainer.Ext().StoredClass(m_className).GetIDs().Length;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return 0;
            }
        }

        public Hashtable  GetFields()
        {
            try
            {
                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_className);//objectContainer.Ext().Reflector().ForName(className);
                IReflectField[] rFields = DataLayerCommon.getDeclaredFieldsInHeirarchy(rClass);
                Hashtable FieldList = new Hashtable();

                foreach (IReflectField field in rFields)
                {
                    if (!FieldList.ContainsKey(field.GetName()))
                    {
                        string stringType = field.GetFieldType().GetName();
                        FieldList.Add(field.GetName(), stringType);
                    }
                }
                return FieldList;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null; 
            }
        }

        public int GetFieldCount()
        {
            try
            {



                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_className);
                if (rClass != null)
                {

                    string type1 = rClass.ToString();
                    type1 = DataLayerCommon.PrimitiveType(type1);
                    char[] arr = CommonValues.charArray;
                        //new char[] { 'G', 'e', 'n', 'e', 'r', 'i', 'c', 'C', 'l', 'a', 's', 's' };
                    type1 = type1.Trim(arr);
                    if (!CommonValues.IsPrimitive(type1) && !type1.Contains(BusinessConstants.DB4OBJECTS_SYS))
                    {
                        IReflectField[] rFields = DataLayerCommon.getDeclaredFieldsInHeirarchy(rClass);
                        return rFields.Length;
                    }
                }
                return 0;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return 0; 
            }
        }

        public  IReflectField[] GetFieldList()
        {
            try
            {


                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_className); //objectContainer.Ext().Reflector().ForName(className);
                IReflectField[] rFields = DataLayerCommon.getDeclaredFieldsInHeirarchy(rClass);
                return rFields;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
           
        }      
    }
}
