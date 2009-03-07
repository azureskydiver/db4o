using System;
using System.Collections;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using OManager.BusinessLayer.UIHelper;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Common;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Reflection;
using OME.Logging.Common;

namespace OManager.DataLayer.QueryParser
{
    
    public class IObjectsetConverter
    {
        private readonly List<Hashtable> m_lstRowContent = new List<Hashtable>();
        private Hashtable m_hashRowContent;
        private readonly IObjectContainer objectContainer;
        private readonly bool m_refresh;

        private readonly string m_classname;

        public IObjectsetConverter( string classname, bool refresh)
        {
            
            m_classname = classname;
            m_refresh = refresh;
            objectContainer=Db4oClient.Client;
        }

		public List<Hashtable> ObjectIDToUIObjects(PagingData pgdata, Hashtable attribList)
        {
            try
            {
                Hashtable hashFieldValue;
                object rowObj=null ;
                if (pgdata.ObjectId.Count < pgdata.EndIndex )
                {
                    pgdata.EndIndex = pgdata.ObjectId.Count;
                }
                if (attribList.Count == 0)
                {
                	IReflectClass rClass = DataLayerCommon.ReflectClassForName(m_classname);
                    if (rClass != null)
                    {
                        IReflectField[] reff = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rClass);
                    	if (reff == null) 
							return m_lstRowContent;

                    	for (int i = pgdata.StartIndex; i < pgdata.EndIndex ; i++)
                    	{
                    		if (pgdata.ObjectId[i] == 0) 
								continue;

                    		rowObj = objectContainer.Ext().GetByID(pgdata.ObjectId[i]);
                    		if (m_refresh)
                    		{
                    			objectContainer.Ext().Refresh(rowObj, 1);
                    		}
                    		else
                    		{
                    			objectContainer.Ext().Activate(rowObj, 1);
                    		}

                    		if (rowObj != null)
                    		{
                    			hashFieldValue = checkforprimitives(reff, rowObj);
                    			m_lstRowContent.Add(hashFieldValue);
                    		}
                    	}
                    }
                }
                else
                {
                    int length = 0;
                    foreach (string str in attribList.Keys)
                    {
                        string[] splitstring = str.Split('.');
                        if (splitstring.Length > length)
                        {
                            length = splitstring.Length;
                        }
                    }
                    
                    for (int i = pgdata.StartIndex; i < pgdata.EndIndex; i++)
                    {
                        hashFieldValue = new Hashtable();
                        m_hashRowContent = new Hashtable();

                        foreach (string attribute in attribList.Keys)
                        {
                            if (pgdata.ObjectId[i] != 0)
                            {
                                rowObj = objectContainer.Ext().GetByID(pgdata.ObjectId[i]);
                                if (m_refresh)
                                {
                                    objectContainer.Ext().Refresh(rowObj, length);
                                }
                                else
                                {
                                    objectContainer.Ext().Activate(rowObj, length);
                                }

                                hashFieldValue = UpdateResults(rowObj, attribute);
                            }
                        }
                        if (hashFieldValue.Count != 0)
                        {
                            if (!hashFieldValue.ContainsKey(BusinessConstants.DB4OBJECTS_REF))
                            {
                                hashFieldValue.Add(BusinessConstants.DB4OBJECTS_REF, rowObj);
                            }
                            m_lstRowContent.Add(hashFieldValue);
                        }

                    }
                }

                return m_lstRowContent;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

        public Hashtable UpdateResults(object rowObject, string attribute)
        {
            try
            {
                drillToObject(rowObject, attribute);                
                return m_hashRowContent;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null ;
            }
        }

        public void drillToObject(object parentobj, string attribute)
        {
            try
            {
                string fieldName = attribute;
                int intIndexof = fieldName.IndexOf('.') + 1;
                fieldName = fieldName.Substring(intIndexof, fieldName.Length - intIndexof);

                string[] splitstring = fieldName.Split('.');
                object holdParentObject = parentobj;
                foreach (string str in splitstring)
                {
                    if (holdParentObject != null)
                    {
                        holdParentObject = getClass(str, holdParentObject, attribute);
                        if (holdParentObject == null)
                            break;
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
               
            }
        }


        public object getClass(string attribName, object subObject, string fullattribName )
        {
            try
            {
                IReflectClass rclass = DataLayerCommon.ReflectClassFor(subObject);
                if (rclass != null)
                {
                    IReflectField rfield = DataLayerCommon.GetDeclaredFieldInHeirarchy(rclass, attribName);
                    if (rfield != null)
                    {

                        string fieldType = rfield.GetFieldType().GetName();
                        int index = fieldType.IndexOf(',');
                        fieldType = fieldType.Substring(0, index);
                        if (m_hashRowContent == null)
                        {
                            m_hashRowContent = new Hashtable();
                        }
                        if (!CommonValues.IsPrimitive(fieldType))
                        {
                            if (!rfield.GetFieldType().IsCollection() && !rfield.GetFieldType().IsArray())
                            {                                
                                object getObj = rfield.Get(subObject);
                                if (getObj == null)
                                {

                                    m_hashRowContent.Add(fullattribName, BusinessConstants.DB4OBJECTS_NULL);

                                }
                                return getObj;
                            }
                        }
                        else
                        {                            
                            object objValue = rfield.Get(subObject);                            
                            m_hashRowContent.Add(fullattribName, objValue);
                        }
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
              
            }
            return null;
        }

        public Hashtable ResultsWithAttributes(object obj,Hashtable attributeList)
        {
            try
            {
                Hashtable hash = new Hashtable();

                if (attributeList.Count != 0)
                {
                    foreach (string attribute in attributeList.Keys)
                    {                           

                        hash = UpdateResults(obj, attribute);
                    }
                    if (hash != null)
                        hash.Add(BusinessConstants.DB4OBJECTS_REF, obj);
                }
                else
                {

                    IReflectClass rClass = DataLayerCommon.ReflectClassForName(m_classname); 
                    if (rClass != null)
                    {
                        IReflectField[] reff = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rClass);
                        if (reff != null)
                        {
                            hash = checkforprimitives(reff, obj);
                        }
                    }
                }
                return hash;
            }

            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

        public Hashtable checkforprimitives(IReflectField[] reff, object obj)
        {
            try
            {
            	Hashtable hash = new Hashtable();
                hash.Add(BusinessConstants.DB4OBJECTS_REF, obj);
                foreach (IReflectField reflectField in reff)
                {
                    string name = reflectField.GetName();
                    object value = reflectField.Get(obj);

                	IType type = Db4oClient.TypeResolver.Resolve(reflectField.GetFieldType());
                    if (value != null)
                    {
                        if (!type.IsEditable)
                        {
                            value = reflectField.GetFieldType();
                        }
                        
						if (!hash.ContainsKey(name))
                        {
                            string gridValue = value.ToString();
                            if (gridValue.StartsWith(BusinessConstants.DB4OBJECTS_GCLASS))
                                gridValue = gridValue.Remove(0, BusinessConstants.DB4OBJECTS_GCLASS.Length);  
                            hash.Add(name, gridValue);
                        }
                    }
                    else
                    {
                        if (!hash.ContainsKey(name))
                            hash.Add(name, BusinessConstants.DB4OBJECTS_NULL);
                    }
                }
                return hash;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
    }
}
