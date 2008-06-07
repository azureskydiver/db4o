using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext ;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.Connection;
using OManager.DataLayer.CommonDatalayer;
using OME.Logging.Common;
using OME.Logging.Tracing;
using System.Reflection; 

namespace OManager.DataLayer.Modal
{
    class FieldDetails 
    {
        private IObjectContainer objectContainer;
        private string m_classname; 
        private string m_fieldname;

        public FieldDetails(string classname, string fieldname)
        {
            string CorrectfieldName = fieldname;
            int intIndexof = CorrectfieldName.ToString().IndexOf('.') + 1;
            CorrectfieldName = CorrectfieldName.Substring(intIndexof, CorrectfieldName.Length - intIndexof);

            this.m_classname = DataLayerCommon.RemoveGFromClassName(classname);
            this.m_fieldname = CorrectfieldName;
            objectContainer = Db4oClient.Client; 

        }
        public bool IsIndexed()
        {
            try
            {
                IStoredClass storedClass = objectContainer.Ext().StoredClass(m_classname);
                if (storedClass != null)
                {
                    IStoredField field = DataLayerCommon.getDeclaredStoredFieldInHeirarchy(storedClass, m_fieldname);
                    return field.HasIndex();
                }
                else
                {
                    return false;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }

        public  bool IsPrimitive()
        {
            try
            {             
                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_classname);
                IReflectField rField = DataLayerCommon.getDeclaredFieldInHeirarchy(rClass, m_fieldname);
                return rField.GetFieldType().IsPrimitive();
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false ;
            }
        }

        public bool GetModifier()
        {
            try
            {
                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_classname);
                if (rClass != null)
                {
                    IReflectField rField = DataLayerCommon.getDeclaredFieldInHeirarchy(rClass, m_fieldname);
                   
                    
                    if (rField != null)
                    {

                        return rField.IsPublic();
                    }
                }
                return false; 
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }

        public bool IsCollection()
        {
            try
            {
                bool isCollection = false;
                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_classname);
                if (rClass != null)
                {
                    IReflectField rField = DataLayerCommon.getDeclaredFieldInHeirarchy(rClass, m_fieldname);
                    if (rField != null)
                    {
                        isCollection = rField.GetFieldType().IsCollection();
                        return isCollection;
                    }

                }
                return isCollection;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }

        public bool IsArray()
        {
            try
            {
                bool isArray = false;
                IReflectClass rClass = DataLayerCommon.returnReflectClass(m_classname);
                if (rClass != null)
                {
                    IReflectField rField = DataLayerCommon.getDeclaredFieldInHeirarchy(rClass, m_fieldname);
                    if (rField != null)
                    {
                        isArray = rField.GetFieldType().IsArray();
                        return isArray;
                    }
                }
                  return isArray;
            }

              
            
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }       

        public string GetDataType()
        {
            try
            {              
            
                if (m_classname != string.Empty)
                {
                    GenericReflector gen = objectContainer.Ext().Reflector();
                    if (gen != null)
                    {

                        IReflectClass rclass = gen.ForName(m_classname);
                        if (rclass != null)
                        {
                            IReflectField rfield = DataLayerCommon.getDeclaredFieldInHeirarchy(rclass, m_fieldname);

                            if (rfield != null)
                            {
                                string str=rfield.GetFieldType().GetName();
                                return DataLayerCommon.PrimitiveType(str);
                                
                            }
                        }
                    }
                }
                return string.Empty; 

            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return string.Empty; 
            }
            
        }     
    }
}
