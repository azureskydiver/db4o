using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.Modal;
using OManager.BusinessLayer.QueryManager;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Common;
using OManager.DataLayer.CommonDatalayer;

using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.ObjectsModification
{
    public class ModifyCollections
    {
        private IObjectContainer objectContainer = null;
        public ModifyCollections()
        {
            objectContainer = Db4oClient.Client;
        }

        public object EditCollections(ArrayList objList, ArrayList offsetList, ArrayList name, ArrayList type, object value)
        {
            try
            {

                SetFieldForCollection(objList, offsetList, name, type, value);
                return objList[0];
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
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

        public void SetFieldForCollection(ArrayList objList, ArrayList offsetList, ArrayList name, ArrayList type, object value)
        {
            try
            {
                object obj;
                for (int i = 0; i < objList.Count; i++)
                {
                    if (objList[i] != null)
                    {
                        if (checkForCollections(objList[i]))
                        {
                            int offset = (int)offsetList[i];

                            object col = (object)objList[i];

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
                                    saveValues(obj, (string)name[i], value, (int)offsetList[i]);
                                    break;
                                }

                            }
                            else if (col is Hashtable || col is IDictionary)
                            {
                                if (col is Hashtable)
                                {
                                    DictionaryEntry dict = (DictionaryEntry)objList[i + 1];
                                    object key = dict.Key;
                                    saveDictionary(objList[i - 1], (string)name[i], value, key);
                                    break;

                                }
                                else if (col is IDictionary)
                                {
                                    object dictKey = null;
                                    IDictionary dict = (IDictionary)col;
                                    foreach (DictionaryEntry keys in dict)
                                    {
                                        dictKey = keys.Key;
                                        break;
                                    }

                                    saveDictionary(objList[i - 1], (string)name[i], value, dictKey);
                                }
                            }

                        }
                        else
                        {
                            if (CommonValues.IsPrimitive((string)type[i]))
                            {
                                obj = objList[i - 1];
                                saveValues(obj, (string)name[i], value, (int)offsetList[i - 1]);

                            }
                        }

                    }
                }
            }

            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);

            }

        }


        public void SetFieldToNull(object parent, string field )
        {
           
            IReflectClass refClass = DataLayerCommon.returnReflectClassfromObject(parent);
            if (refClass != null)
            {
                IReflectField rfield = DataLayerCommon.getDeclaredField(refClass, field);
                if (refClass != null)
                {
                    rfield.Set(parent, null);
                    objectContainer.Set(parent);
                    objectContainer.Commit();
                }
            }
        }

        public void saveDictionary(object currObject, string attribName, object fieldValue, object key)
        {
            try
            {
                IReflectClass rclass = DataLayerCommon.returnReflectClassfromObject(currObject);// objectContainer.Ext().Reflector().ForObject(currObject);
                if (rclass != null)
                {
                    IReflectField rfield = DataLayerCommon.getDeclaredFieldInHeirarchy(rclass, attribName);
                    if (rfield != null)
                    {
                        if (!(rfield is GenericVirtualField))
                        {

                            string fieldType = rfield.GetFieldType().GetName();
                            fieldType = DataLayerCommon.PrimitiveType(fieldType);

                            object objValue1 = rfield.Get(currObject);
                            ICollection col = (ICollection)objValue1;
                            if (col is Hashtable)
                            {
                                Hashtable hash = (Hashtable)col;
                                hash.Remove(key);
                                hash.Add(key, fieldValue);
                                rfield.Set(currObject, hash);
                            }
                            else if (col is IDictionary)
                            {
                                IDictionary dict = (IDictionary)col;
                                dict.Remove(key);
                                dict.Add(key, fieldValue);
                                rfield.Set(currObject, dict);
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
        public void SetArrayListValue(IList list, int offset, string fieldType, object fieldValue)
        {
            switch (fieldType)
            {
                case BusinessConstants.INT16:
                    list[offset] = Convert.ToInt16(fieldValue);
                    break;
                case BusinessConstants.INT32:
                    list[offset] = Convert.ToInt32(fieldValue);
                    break;
                case BusinessConstants.INT64:
                    list[offset] = Convert.ToInt64(fieldValue);
                    break;
                case BusinessConstants.SINGLE:
                    list[offset] = Convert.ToSingle(fieldValue);
                    break;
                case BusinessConstants.DOUBLE:
                    list[offset] = Convert.ToDouble(fieldValue);
                    break;
                case BusinessConstants.DECIMAL:
                    list[offset] = Convert.ToDecimal(fieldValue);
                    break;
                case BusinessConstants.BYTE:
                    list[offset] = Convert.ToByte(fieldValue);
                    break;
                case BusinessConstants.SBYTE:
                    list[offset] = Convert.ToSByte(fieldValue);
                    break;
                case BusinessConstants.CHAR:
                    list[offset] = Convert.ToChar(fieldValue);
                    break;
                case BusinessConstants.DATETIME:
                    list[offset] = Convert.ToDateTime(fieldValue);
                    break;
                case BusinessConstants.BOOLEAN:
                    list[offset] = Convert.ToBoolean(fieldValue);
                    break;
                case BusinessConstants.UINT16:
                    list[offset] = Convert.ToUInt16(fieldValue);
                    break;
                case BusinessConstants.UINT32:
                    list[offset] = Convert.ToUInt32(fieldValue);
                    break;
                case BusinessConstants.UINT64:
                    list[offset] = Convert.ToUInt64(fieldValue);
                    break;

                default:
                    list[offset] = fieldValue;
                    break;
            }


        }
        public void SetArrayValue(IReflectField rfield, int offset, object objValue, string fieldType, object fieldValue)
        {
            switch (fieldType)
            {
                case BusinessConstants.DOUBLEARRAY:
                    ((double[])objValue)[offset] = Convert.ToDouble(fieldValue);
                    break;
                case BusinessConstants.DECIMALARRAY:
                    ((decimal[])objValue)[offset] = Convert.ToDecimal(fieldValue);
                    break;
                case BusinessConstants.SINGLEARRAY:
                    ((Single[])objValue)[offset] = Convert.ToSingle(fieldValue);
                    break;
                case BusinessConstants.INT16ARRAY:
                    ((Int16[])objValue)[offset] = Convert.ToInt16(fieldValue);
                    break;
                case BusinessConstants.INT32ARRAY:
                    ((Int32[])objValue)[offset] = Convert.ToInt32(fieldValue);
                    break;
                case BusinessConstants.UINT16ARRAY:
                    ((UInt16[])objValue)[offset] = Convert.ToUInt16(fieldValue);
                    break;
                case BusinessConstants.UINT32ARRAY:
                    ((UInt32[])objValue)[offset] = Convert.ToUInt32(fieldValue);
                    break;
                case BusinessConstants.INT64ARRAY:
                    ((Int64[])objValue)[offset] = Convert.ToInt64(fieldValue);
                    break;
                case BusinessConstants.UINT64RRAY:
                    ((UInt64[])objValue)[offset] = Convert.ToUInt64(fieldValue);
                    break;
                case BusinessConstants.CHARARRAY:
                    ((char[])objValue)[offset] = Convert.ToChar(fieldValue);
                    break;
                case BusinessConstants.BYTEARRAY:
                    ((byte[])objValue)[offset] = Convert.ToByte(fieldValue);
                    break;
                case BusinessConstants.SBYTEARRAY:
                    ((sbyte[])objValue)[offset] = Convert.ToSByte(fieldValue);
                    break;
                case BusinessConstants.BOOLEANARRAY:
                    ((bool[])objValue)[offset] = Convert.ToBoolean(fieldValue);
                    break;
                case BusinessConstants.DATETIMEARRAY:
                    ((DateTime[])objValue)[offset] = Convert.ToDateTime(fieldValue);
                    break;
                default:
                    ((object[])objValue)[offset] = fieldValue;
                    break;
            }


        }
        public void saveValues(object currObject, string attribName, object fieldValue, int offset)
        {

            try
            {
                IReflectClass rclass = DataLayerCommon.returnReflectClassfromObject(currObject);// objectContainer.Ext().Reflector().ForObject(currObject);
                IReflectField rfield = DataLayerCommon.getDeclaredFieldInHeirarchy(rclass, attribName);
                if (rfield != null && !(rfield is GenericVirtualField))
                {
                    string fieldType = rfield.GetFieldType().GetName();
                    fieldType = DataLayerCommon.PrimitiveType(fieldType);
                    if (!CommonValues.IsPrimitive(fieldType))
                    {
                        object objValue1 = rfield.Get(currObject);
                        if (rfield.GetFieldType().IsArray())
                        {
                            SetArrayValue(rfield, offset, objValue1, fieldType, fieldValue);
                            rfield.Set(currObject, objValue1);
                        }
                        else if (rfield.GetFieldType().IsCollection())
                        {
                            ICollection col = (ICollection)objValue1;
                            if (col is IList)
                            {
                                IList list = (ArrayList)col;
                                SetArrayListValue(list, offset, fieldType, fieldValue);
                                rfield.Set(currObject, list);
                            }
                        }
                    }
                    else
                    {
                        setObject(rfield, currObject, fieldType, fieldValue);
                    }
                }
            }
            catch (Exception oEx)
            {
                objectContainer.Rollback();
                LoggingHelper.HandleException(oEx);

            }

        }
        public bool checkForCollections(object obj)
        {
            try
            {
                if (DataLayerCommon.IsCollection(obj) || DataLayerCommon.IsArray(obj))
                    return true;
                else
                    return false;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }
        public void setObject(IReflectField rfield, object subObject, string fieldType, object fieldValue)
        {
            try
            {
                switch (fieldType)
                {

                    case BusinessConstants.INT32:
                        rfield.Set(subObject, Convert.ToInt32(fieldValue));
                        break;
                    case BusinessConstants.INT64:
                        rfield.Set(subObject, Convert.ToInt64(fieldValue));
                        break;
                    case BusinessConstants.DOUBLE:
                        rfield.Set(subObject, Convert.ToDouble(fieldValue));
                        break;
                    case BusinessConstants.DECIMAL:
                        rfield.Set(subObject, Convert.ToDecimal(fieldValue));
                        break;
                    case BusinessConstants.INT16:
                        rfield.Set(subObject, Convert.ToInt16(fieldValue));
                        break;
                    case BusinessConstants.CHAR:
                        rfield.Set(subObject, Convert.ToChar(fieldValue));
                        break;
                    case BusinessConstants.BYTE:
                        rfield.Set(subObject, Convert.ToByte(fieldValue));
                        break;
                    case BusinessConstants.DATETIME:
                        rfield.Set(subObject, Convert.ToDateTime(fieldValue));
                        break;
                    case BusinessConstants.BOOLEAN:
                        rfield.Set(subObject, Convert.ToBoolean(fieldValue));
                        break;
                    case BusinessConstants.SINGLE:
                        rfield.Set(subObject, Convert.ToSingle(fieldValue));
                        break;
                    case BusinessConstants.SBYTE:
                        rfield.Set(subObject, Convert.ToSByte(fieldValue));
                        break;
                    case BusinessConstants.UINT16:
                        rfield.Set(subObject, Convert.ToUInt16(fieldValue));
                        break;
                    case BusinessConstants.UINT32:
                        rfield.Set(subObject, Convert.ToUInt32(fieldValue));
                        break;
                    case BusinessConstants.UINT64:
                        rfield.Set(subObject, Convert.ToUInt64(fieldValue));
                        break;
                    default:
                        rfield.Set(subObject, fieldValue);
                        break;
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
