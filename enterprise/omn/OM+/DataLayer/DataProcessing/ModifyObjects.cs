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
        private List<object> m_listforCascadeDelete;
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

    	public void CascadeonDelete( bool checkforCascade)
        {
            try
            {
                if (checkforCascade)
                {
                    if (m_objectToProcess != null)
                    {
                        m_listforCascadeDelete = new List<object>();
                        CheckForHierarchy(m_objectToProcess);
                        objectContainer.Delete(m_objectToProcess);
                        objectContainer.Commit();
                    }

                }
                else
                {
                    if (m_objectToProcess != null)
                    {
                        objectContainer.Delete(m_objectToProcess);
                        objectContainer.Commit();
                    }
                }
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();  
                LoggingHelper.HandleException(oEx);
                
            }
        }

        private void CheckForHierarchy(object obj)
        {
            try
            {
                IReflectClass rclass = DataLayerCommon.ReflectClassFor(obj);
                if (rclass != null)
                {
                    IReflectField[] fieldArr = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rclass);
                    if (fieldArr != null)
                    {
                        foreach (IReflectField field in fieldArr)
                        {

                            object getObject = field.Get(obj);
                            string fieldType = field.GetFieldType().GetName();
                            fieldType = DataLayerCommon.PrimitiveType(fieldType);
                            if (getObject != null)
                            {
                                if (!CommonValues.IsPrimitive(fieldType))
                                {
                                    if (field.GetFieldType().IsCollection())
                                    {
                                        ICollection coll = (ICollection)field.Get(obj);
                                        ArrayList arrList = new ArrayList(coll);

                                        for (int i = 0; i < arrList.Count; i++)
                                        {
                                            object colObject = arrList[i];
                                            if (colObject != null)
                                            {
                                                if (colObject is GenericObject)
                                                {
                                                    if (!m_listforCascadeDelete.Contains(colObject))
                                                    {
                                                        m_listforCascadeDelete.Add(colObject);
                                                        CheckForHierarchy(colObject);
                                                        objectContainer.Delete(colObject);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (field.GetFieldType().IsArray())
                                    {
                                        int length = objectContainer.Ext().Reflector().Array().GetLength(field.Get(obj));
                                        for (int i = 0; i < length; i++)
                                        {
                                            object arrObject = objectContainer.Ext().Reflector().Array().Get(field.Get(obj), i);
                                            if (arrObject != null)
                                            {
                                                if (arrObject is GenericObject)
                                                {
                                                    if (!m_listforCascadeDelete.Contains(arrObject))
                                                    {
                                                        m_listforCascadeDelete.Add(arrObject);
                                                        CheckForHierarchy(arrObject);
                                                        objectContainer.Delete(arrObject);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if (!m_listforCascadeDelete.Contains(getObject))
                                        {
                                            m_listforCascadeDelete.Add(getObject);
                                            CheckForHierarchy(getObject);
                                            objectContainer.Delete(getObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
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
