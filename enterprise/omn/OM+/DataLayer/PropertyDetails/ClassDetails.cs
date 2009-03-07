using System;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using OManager.DataLayer.Connection;
using OManager.DataLayer.CommonDatalayer;
using OManager.BusinessLayer.Common;

using OME.Logging.Common;

namespace OManager.DataLayer.Modal
{
    class ClassDetails 
    {
        private readonly IObjectContainer objectContainer;
        private readonly string m_className;

        public  ClassDetails(string className)
        {
            m_className = DataLayerCommon.RemoveGFromClassName(className);
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

        public Hashtable GetFields()
        {
            try
            {
                IReflectClass rClass = DataLayerCommon.ReflectClassForName(m_className);
                IReflectField[] rFields = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rClass);
                Hashtable FieldList = new Hashtable();

                foreach (IReflectField field in rFields)
                {
                    if (!FieldList.ContainsKey(field.GetName()))
                    {
                    	FieldList.Add(
							field.GetName(), 
							field.GetFieldType().GetName());
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
                IReflectClass rClass = DataLayerCommon.ReflectClassForName(m_className);
                if (rClass != null)
                {
                    string type1 = rClass.ToString();
                    type1 = DataLayerCommon.PrimitiveType(type1);
                    char[] arr = CommonValues.charArray;
                    type1 = type1.Trim(arr);
                    if (!CommonValues.IsPrimitive(type1) && !type1.Contains(BusinessConstants.DB4OBJECTS_SYS))
                    {
                        IReflectField[] rFields = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rClass);
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

        public IReflectField[] GetFieldList()
        {
            try
            {
                IReflectClass rClass = DataLayerCommon.ReflectClassForName(m_className); 
                IReflectField[] rFields = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rClass);
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
