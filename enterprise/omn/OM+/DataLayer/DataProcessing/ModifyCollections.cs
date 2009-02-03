using System;
using System.Collections;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.Connection;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Reflection;
using OME.Logging.Common;

namespace OManager.DataLayer.ObjectsModification
{
    public class ModifyCollections
    {
        private readonly IObjectContainer objectContainer;
        public ModifyCollections()
        {
            objectContainer = Db4oClient.Client;
        }

        public void EditCollections(IList objList, IList<int> offsetList, IList<string> names, IList<IType> types, object value)
        {
            try
            {
                SetFieldForCollection(objList, offsetList, names, types, value);
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public void SaveCollections(object topObject, int level)
        {
            try
            {
                objectContainer.Ext().Set(topObject, level);
                objectContainer.Commit();
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();
                LoggingHelper.HandleException(oEx);

            }
        }

        public void SetFieldForCollection(IList objList, IList<int> offsetList, IList<string> names, IList<IType> types, object value)
        {
            try
            {
            	for (int i = 0; i < objList.Count; i++)
                {
                	if ( (objList[i]) == null && !types[i].IsNullable) 
						continue;

                	object obj;
                	if (CheckForCollections(objList[i]))
                	{
                		int offset = offsetList[i];

                		object col = objList[i];

                		if (offsetList[i + 1].Equals(-2))
                		{
                			if (col is Array || col is IList)
                			{
                				if (objList[i - 1] is IList)
                				{
                					obj = ((IList)objList[i - 1])[offset];
                				}
                				else
                				{
                					obj = objList[i - 1];
                				}
                				SaveValues(obj, names[i], value, offsetList[i], types[i]);
                				break;
                			}

                		}
                		else if (col is Hashtable || col is IDictionary)
                		{
                			if (col is Hashtable)
                			{
                				DictionaryEntry dict = (DictionaryEntry)objList[i + 1];
                				SaveDictionary(objList[i - 1], names[i], value, dict.Key);
                				break;

                			}

                			if (col is IDictionary)
                			{
                				object dictKey = null;
                				foreach (DictionaryEntry entry in (IDictionary)col)
                				{
                					dictKey = entry.Key;
                					break;
                				}

                				SaveDictionary(objList[i - 1], names[i], value, dictKey);
                			}
                		}
                	}
                	else
                	{
                		if (types[i].IsEditable)
                		{
                			obj = objList[i - 1];
                			SaveValues(obj, names[i], value, offsetList[i - 1], types[i]);
                		}
                	}
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

    	public void SaveDictionary(object targetObject, string attribName, object newValue, object key)
        {
            try
            {
                IReflectClass rclass = DataLayerCommon.ReflectClassFor(targetObject);
                if (rclass != null)
                {
                    IReflectField rfield = DataLayerCommon.GetDeclaredFieldInHeirarchy(rclass, attribName);
                    if (rfield != null)
                    {
                        if (!(rfield is GenericVirtualField))
                        {
                            object objValue1 = rfield.Get(targetObject);
                            ICollection col = (ICollection) objValue1;
                            if (col is Hashtable)
                            {
                                Hashtable hash = (Hashtable)col;
                                hash.Remove(key);
                                hash.Add(key, newValue);
                                rfield.Set(targetObject, hash);
                            }
                            else if (col is IDictionary)
                            {
                                IDictionary dict = (IDictionary)col;
                                dict.Remove(key);
                                dict.Add(key, newValue);
                                rfield.Set(targetObject, dict);
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

    	public void SaveValues(object targetObject, string attribName, object newValue, int offset, IType type)
        {
            try
            {
                IReflectClass rclass = DataLayerCommon.ReflectClassFor(targetObject);
                IReflectField rfield = DataLayerCommon.GetDeclaredFieldInHeirarchy(rclass, attribName);
                if (rfield != null && !(rfield is GenericVirtualField))
                {
                    if (type.IsArray || type.IsCollection)
                    {
                    	IList list = rfield.Get(targetObject) as IList;
						if (null != list)
                        {
							list[offset] = newValue;
							rfield.Set(targetObject, list);
                        }
                    }
                    else
                    {
						SetObject(rfield, targetObject, type, newValue);
                    }
                }
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();
                LoggingHelper.HandleException(oEx);
            }
        }

        public bool CheckForCollections(object obj)
        {
            try
            {
                return DataLayerCommon.IsCollection(obj) || DataLayerCommon.IsArray(obj);
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }

        public void SetObject(IReflectField rfield, object containingObject, IType fieldType, object newValue)
        {
            try
            {
				rfield.Set(containingObject, fieldType.Cast(newValue));
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();
                LoggingHelper.HandleException(oEx);
            }
        }
    }
}
