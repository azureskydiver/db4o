using System;
using System.Collections;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Common;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Reflection;
using OME.Logging.Common;

namespace OManager.DataLayer.ObjectsModification
{
    class ModifyObjects
    {
        private readonly IObjectContainer objectContainer;
		private readonly object m_objectToProcess;

        public ModifyObjects(object MainObjects)
        {
            objectContainer = Db4oClient.Client;
            m_objectToProcess = MainObjects;
        }

        public static object EditObjects(object obj, string attribute, string value)
        {
            try
            {
                string fieldName = attribute;
                int intIndexof = fieldName.IndexOf('.') + 1;
                fieldName = fieldName.Substring(intIndexof, fieldName.Length - intIndexof);

                string[] splitstring = fieldName.Split('.');
                object holdParentObject = obj;

                foreach (string str in splitstring)
                {
                    holdParentObject = SetField(str, holdParentObject, fieldName, value);
                }

                return obj;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
       
        private static object SetField(string attribName, object subObject, string fullattribName, string fieldValue)
        {
            try
            {
            	IReflectClass rclass = DataLayerCommon.ReflectClassFor(subObject);
            	if (rclass == null)
            		return null;

            	IReflectField rfield = DataLayerCommon.GetDeclaredFieldInHeirarchy(rclass, attribName);
            	if (rfield == null)
            		return null;

            	if (rfield is GenericVirtualField)
            		return null;

            	IType fieldType = Db4oClient.TypeResolver.Resolve(rfield.GetFieldType());
            	if (!fieldType.IsEditable)
            	{
					if (!fieldType.IsCollection && !fieldType.IsArray)
            			return rfield.Get(subObject);
            	}
            	else if (subObject != null)
				{
					SetObject(rfield, subObject, fieldType, fieldValue);
            	}

            	return null;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

		public static void SetObject(IReflectField rfield, object containingObject, IType fieldType, string newValue)
        {
            try
            {
				rfield.Set(containingObject, fieldType.Cast(newValue));
            }
            catch (Exception oEx)
            {
                Db4oClient.Client.Rollback();
                LoggingHelper.HandleException(oEx);
            }
        }

        public static object SaveObjects(object obj)
        {
        	IObjectContainer objectContainer = Db4oClient.Client;
			try
            {
            	if (obj == null)
            		return obj;
            	
				objectContainer.Store(obj);
            	objectContainer.Commit();
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();
                LoggingHelper.HandleException(oEx);

            }
            return obj;
        }

    	public void DeleteObject()
        {
            try
            {
               
                    if (m_objectToProcess != null)
                    {
                        objectContainer.Delete(m_objectToProcess);
                    	objectContainer.Ext().Purge();
                        objectContainer.Commit();
                    }
                
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();  
                LoggingHelper.HandleException(oEx);
                
            }
        }

        
    }
}
