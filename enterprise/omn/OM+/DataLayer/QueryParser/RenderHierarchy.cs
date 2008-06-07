using System;
using System.Collections;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic ;
using Db4objects.Db4o.Reflect.Net;
using System.Text;
using System.Windows.Forms;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Common;
using OME.AdvancedDataGridView;
using System.Drawing;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Modal;
using OManager.DataLayer.ObjectsModification;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.QueryParser
{
    public class RenderHierarchy
    {
        TreeGridView m_treeGridView = null;
        TreeGridColumn m_fieldColumn = null;
        TreeGridViewDateTimePickerColumn m_valueColumn = null;
        DataGridViewTextBoxColumn m_typeColumn = null;
        ImageList m_imageListTreeGrid = null;

        IObjectContainer container;
        public RenderHierarchy()
        { 
            container = Db4oClient.Client;
        }
        public TreeGridView ReturnHierarchy(object currObj,string classname,bool check)
        {
           
            InitializeImageList();
            InitializeTreeGridView();
            TreeGridNode rootNode = new TreeGridNode();
            m_treeGridView.Nodes.Add(rootNode);

            try
            {
                IReflectClass rclass = DataLayerCommon.returnReflectClass(classname);
                string strValue = string.Empty;  
                if (rclass != null)
                {
                     strValue = rclass.GetName();  
                }
                long localid = GetLocalID(currObj);
               
                    rootNode.Cells[0].Value = strValue + " (Object ID : " + localid.ToString()+ " )"; //container.Ext().Reflector().ForObject(currObj).GetName(); //fieldname
             
                rootNode.Cells[2].Value = strValue;//type
                rootNode.Cells[1].Value = ReturnClassName(currObj.ToString());//value
                rootNode.Tag = currObj;
                rootNode.Cells[1].ReadOnly = true;
                rootNode.Expand();
                rootNode.ImageIndex = 0; //Class
                classname = DataLayerCommon.RemoveGFromClassName(classname);   
                TraverseObjTree(ref  rootNode, currObj,classname, check );
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
            return m_treeGridView;
        }

        private void InitializeImageList()
        {
            try
            {
                m_imageListTreeGrid = new ImageList();
                m_imageListTreeGrid.Images.Add(Properties.Resource.treeview_class); //0 Class
                m_imageListTreeGrid.Images.Add(Properties.Resource.treeview_collection); //1 Collections 
                m_imageListTreeGrid.Images.Add(Properties.Resource.treeview_primitive); //2 Primitive
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public void TraverseObjTree(ref TreeGridNode rootNode, object currObj, string classname, bool check)
        {
            string type1 = string.Empty;
            container = Db4oClient.Client;
            IReflectClass refClass = DataLayerCommon.returnReflectClass(classname); //container.Ext().Reflector().ForName(classname); //get reflect class
            if (refClass != null)
            {
                type1 = refClass.ToString(); //to filter Generic class

                if (type1 != string.Empty)
                {
                    type1 = ReturnClassName(type1);
                    char[] arr = CommonValues.charArray;
                        //new char[] { 'G', 'e', 'n', 'e', 'r', 'i', 'c', 'C', 'l', 'a', 's', 's', ' ' };

                    type1 = type1.Trim(arr);

                    try
                    {
                        if (refClass.IsPrimitive() || CommonValues.IsPrimitive(type1))
                        {
                            TreeGridNode node = new TreeGridNode();
                            rootNode.Nodes.Add(node);
                            node.Cells[0].Value = ReturnClassName(container.Ext().Reflector().ForObject(currObj).GetName());
                            node.Cells[1].Value = currObj.ToString();
                            node.Cells[1].ReadOnly = false;
                            node.Cells[2].Value = ReturnClassName(container.Ext().Reflector().ForObject(currObj).GetName());
                            node.Tag = currObj;
                            node.ImageIndex = 2; 

                            return;
                        }
                        if (refClass != null)
                        {

                            IReflectField[] fieldArr = DataLayerCommon.getDeclaredFieldsInHeirarchy(refClass);
                            if (fieldArr != null)
                            {
                                foreach (IReflectField field in fieldArr)
                                {
                                    IReflectClass fieldType = ReturnFieldType(field);
                                    if (fieldType != null)
                                    {
                                        string type = field.GetFieldType().GetName();//remove assembly name to just get the class
                                        type = ReturnClassName(type);
                                        if (CommonValues.IsPrimitive(type)) //if leaf node
                                        {
                                            object obj = field.Get(currObj);
                                           
                                            //if (obj != null)
                                                CreatePrimitiveNode(field.GetName(), obj, ref rootNode, type);
                                        }
                                        else if (fieldType.IsCollection())
                                        {
                                            RenderCollection(rootNode, currObj, field,check);

                                        }
                                        else if (fieldType.IsArray())
                                        {
                                            RenderArray(rootNode, currObj, field,check);
                                        }
                                        else
                                        {
                                            RenderSubObject(rootNode, currObj, field, check);
                                        }
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
            }
        }

        private void RenderArray(TreeGridNode rootNode, object currObj, IReflectField field,bool check)
        {
            container = Db4oClient.Client;
            object obj = field.Get(currObj);
        //    container.Activate(obj, 1);
            if (check == true)
            {
                container.Ext().Activate(obj, 2);
            }
            else
            {
                container.Ext().Refresh(obj, 2);
            }
            if (obj != null)
            {
                int length = container.Ext().Reflector().Array().GetLength(obj);
                CreateCollectionNode(field, obj, ref rootNode, length.ToString());
            }
            else
            {
                CreateCollectionNode(field, obj, ref rootNode, "0");
            }
           
        }

        private  void RenderCollection(TreeGridNode rootNode, object currObj, IReflectField field,bool check)
        {
            container = Db4oClient.Client;
            object obj = field.Get(currObj);
            if (check == true)
            {
                container.Ext().Activate(obj, 2);
            }
            else
            {
                container.Ext().Refresh(obj, 2);
            }
            if (obj != null)
            {
                ICollection coll = (ICollection)obj;
                CreateCollectionNode(field, obj, ref rootNode, coll.Count.ToString());
            }
            else
            {
                CreateCollectionNode(field, obj, ref rootNode, "0");
            }
           
        }

        private void RenderSubObject(TreeGridNode rootNode, object currObj, IReflectField field, bool check)
        {
            try
            {
                container = Db4oClient.Client;
                TreeGridNode objectNode = new TreeGridNode();
                rootNode.Nodes.Add(objectNode);
                object obj = field.Get(currObj);
                long localid = GetLocalID(obj);
             
                objectNode.Cells[0].Value = field.GetName() + " (Object ID : " + localid.ToString()+" )";
           
                if (field.Get(currObj) != null)

                    objectNode.Cells[1].Value = field.Get(currObj);
                else
                    objectNode.Cells[1].Value = BusinessConstants.DB4OBJECTS_NULL;

                objectNode.Cells[2].Value = ReturnClassName(field.GetFieldType().GetName());
                
                if (check == true)
                {
                    container.Ext().Activate(obj, 2);
                }
                else
                {
                    container.Ext().Refresh(obj, 2);
                }
             //   container.Activate(obj,2); 

                objectNode.Tag = obj;

                if (rootNode.Tag is DictionaryEntry && field.GetName() == BusinessConstants.DB4OBJECTS_KEY)
                    objectNode.Cells[1].ReadOnly = true;

                else if (rootNode.Tag is DictionaryEntry && field.GetName() == BusinessConstants.DB4OBJECTS_VALUE)
                    objectNode.Cells[1].ReadOnly = false;
                else if (field.Get(currObj) == null)
                    objectNode.Cells[1].ReadOnly = true;
                else
                    objectNode.Cells[1].ReadOnly = true;

                objectNode.ImageIndex = 0;//class
                if (obj != null)
                {
                    TreeGridNode treenodeDummyChildNode = new TreeGridNode();
                    objectNode.Nodes.Add(treenodeDummyChildNode);
                    treenodeDummyChildNode.Cells[0].Value = BusinessConstants.DB4OBJECTS_DUMMY;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                
            }

        }

        private bool CheckIfNodeAlreadyExists(TreeGridNode parent, string name,string value)
        {
            try
            {
                foreach (TreeGridNode node in parent.Nodes)
                {
                    if (node.Cells[0].Value.Equals(name) && node.Cells[1].Value.Equals(value))
                        return true;
                }
                return false;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
        }
        private string ReturnClassName(string type)
        {
            try
            {
                if (type != "null")
                {
                    type = DataLayerCommon.PrimitiveType(type);
                    return DataLayerCommon.RemoveGFromClassName(type);
                }
                return null;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

        public void CreateCollectionNode(IReflectField field, object currObj, ref TreeGridNode rootNode, string count)
        {
            try
            {
                TreeGridNode collNode = new TreeGridNode();

                rootNode.Nodes.Add(collNode);
                collNode.Cells[1].ReadOnly = true;
                collNode.Cells[0].Value = field.GetName() + " ( " + count + " items )";
                collNode.Cells[2].Value = ReturnClassName(field.GetFieldType().GetName());
                if (currObj != null)
                {
                    collNode.Cells[1].Value = currObj.ToString();
                    collNode.Tag = currObj;
                }
                else
                {
                    collNode.Cells[1].Value = BusinessConstants.DB4OBJECTS_NULL ;
                }
                collNode.ImageIndex = 1; //Collection
                if (Convert.ToInt32(count) > 0)
                {
                    TreeGridNode treenodeDummyChildNode = new TreeGridNode();
                    collNode.Nodes.Add(treenodeDummyChildNode);
                    treenodeDummyChildNode.Cells[0].Value = BusinessConstants.DB4OBJECTS_DUMMY;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public void ExpandCollectionNode(TreeGridNode node)
        {
            try
            {
                container = Db4oClient.Client;
                object CollObj = node.Tag;
                ICollection coll = (ICollection)CollObj;
                ArrayList arrList = new ArrayList(coll);

                                       
                TreeGridNode collNode = null;

                for (int i = 0; i < arrList.Count; i++)
                //foreach (object colObject in coll)
                {

                    if (arrList[i] != null)
                    {
                        if (!DataLayerCommon.IsPrimitive(arrList[i]))
                        {
                            container.Ext().Activate(arrList[i], 1);
                            collNode = new TreeGridNode();
                            node.Nodes.Add(collNode);
                            long localid = GetLocalID(arrList[i]);
                            if (!DataLayerCommon.IsCollection(arrList[i]) && !DataLayerCommon.IsArray(arrList[i]))
                            {
                                collNode.Cells[0].Value = arrList[i].ToString() + " (Object ID : " + localid.ToString() + " )"; ;
                            }
                            else
                            collNode.Cells[0].Value = arrList[i].ToString();
                            collNode.Cells[2].Value = ReturnClassName(arrList[i].ToString());
                            collNode.Cells[1].Value = ReturnClassName(arrList[i].ToString());
                            collNode.Tag = arrList[i];
                            collNode.ImageIndex = 0; //class
                            collNode.Cells[1].ReadOnly = true;
                            TraverseObjTree(ref collNode, arrList[i], container.Ext().Reflector().ForObject(arrList[i]).GetName(), false);
                        }
                        else
                            TraverseObjTree(ref node, arrList[i], container.Ext().Reflector().ForObject(arrList[i]).GetName(), false);
                    }
                }

            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public void ExpandObjectNode(TreeGridNode node,bool checkVal )
        {
            try
            {
                object nodeObject = node.Tag;
                if (nodeObject != null)
                {
                    IReflectClass rclass = DataLayerCommon.returnReflectClassfromObject(nodeObject);
                    if (rclass != null)
                    {
                        string classname = rclass.GetName();
                        if (classname != string.Empty)
                        {
                            TraverseObjTree(ref node, nodeObject, classname, checkVal);
                        }
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public void ExpandArrayNode(TreeGridNode node)
        {
            try
            {
                container = Db4oClient.Client;
                TreeGridNode arrNode =null;
                int length = container.Ext().Reflector().Array().GetLength(node.Tag);
                for (int i = 0; i < length; i++)
                {
                    object nodeObject = container.Ext().Reflector().Array().Get(node.Tag, i);
                    container.Ext().Activate(nodeObject, 1);
                    if (nodeObject != null)
                    {
                        if (!DataLayerCommon.IsPrimitive(nodeObject))
                        {
                            arrNode = new TreeGridNode();
                            node.Nodes.Add(arrNode);
                            long localid = GetLocalID(nodeObject);
                            if (!DataLayerCommon.IsCollection(nodeObject) && !DataLayerCommon.IsArray(nodeObject))
                            {
                                arrNode.Cells[0].Value = nodeObject.ToString() + " (Object ID : " + localid.ToString() + " )"; ;
                            }
                            else
                            arrNode.Cells[0].Value = nodeObject.ToString();
                            arrNode.Cells[2].Value = ReturnClassName(nodeObject.ToString());
                            arrNode.Cells[1].Value = ReturnClassName(nodeObject.ToString());
                            arrNode.Tag = nodeObject;
                            arrNode.ImageIndex = 0;
                            arrNode.Cells[1].ReadOnly = true;
                            TraverseObjTree(ref arrNode, container.Ext().Reflector().Array().Get(node.Tag, i), container.Ext().Reflector().ForObject(nodeObject).GetName(),false );
                        }
                        else
                            TraverseObjTree(ref node, container.Ext().Reflector().Array().Get(node.Tag, i), container.Ext().Reflector().ForObject(nodeObject).GetName(),false );
                        
                    }
                }

            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

        }

        public void CreatePrimitiveNode(string fieldName, object value, ref TreeGridNode rootNode, string type)
        {
            try
            {

                TreeGridNode primitiveNode = new TreeGridNode();
                rootNode.Nodes.Add(primitiveNode);
                primitiveNode.Cells[0].Value = fieldName;
                primitiveNode.Cells[2].Value = type;
                primitiveNode.Cells[1].ReadOnly = false;
                if (rootNode.Parent.Tag is IDictionary)
                {
                    if (fieldName != BusinessConstants.DB4OBJECTS_VALUE1)
                    {
                        primitiveNode.Cells[1].ReadOnly = true;
                    }
                    else
                    {
                        primitiveNode.Cells[1].ReadOnly = false;
                    }
                }

                if (value != null)
                {
                    primitiveNode.Cells[1].Value = value.ToString();
                }
                else
                {
                    primitiveNode.Cells[1].Value = BusinessConstants.DB4OBJECTS_NULL;
                }

                primitiveNode.Tag = value;
                primitiveNode.ImageIndex = 2; //Primitive
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

        public long GetLocalID(object obj)
        {
            ObjectDetails objDetails = new ObjectDetails(obj);
            return objDetails.GetLocalID();
        }

        public IReflectClass ReturnFieldType(IReflectField field)
        {
            return field.GetFieldType();
        }

        private void InitializeTreeGridView()
        {
            try
            {
                //TreeGridView Initialization
                m_treeGridView = new TreeGridView();
                m_treeGridView.Size = new System.Drawing.Size(530, 442);
                m_treeGridView.Location = new System.Drawing.Point(2, 2);
                m_treeGridView.Name = BusinessConstants.DB4OBJECTS_TREEGRIDVIEW;
                m_treeGridView.RowHeadersVisible = false;
                m_treeGridView.ShowLines = true;
            
                //Column Intialization

                //Field Column
                m_fieldColumn = new TreeGridColumn();
                m_fieldColumn.DefaultNodeImage = null;
                m_fieldColumn.FillWeight = 386.9562F;
                m_fieldColumn.HeaderText = BusinessConstants.DB4OBJECTS_FIELD;
                m_fieldColumn.Name = BusinessConstants.DB4OBJECTS_FIELDCOLOUMN;
                m_fieldColumn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
                m_fieldColumn.ReadOnly = true;
                m_fieldColumn.Width = 170;

                //Value Column
                m_valueColumn = new TreeGridViewDateTimePickerColumn();
                m_valueColumn.FillWeight = 50F;
                m_valueColumn.HeaderText = BusinessConstants.DB4OBJECTS_VALUEFORGRID;
                m_valueColumn.Name = BusinessConstants.DB4OBJECTS_VALUECOLUMN; 
                m_valueColumn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
                m_valueColumn.ReadOnly = false;
                m_valueColumn.Width = 150;

                //Type Column
                m_typeColumn = new DataGridViewTextBoxColumn();                
                m_typeColumn.FillWeight = 50F;
                m_typeColumn.HeaderText = BusinessConstants.DB4OBJECTS_TYPE;
                m_typeColumn.Name = BusinessConstants.DB4OBJECTS_TYPECOLUMN; 
                m_typeColumn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
                m_typeColumn.ReadOnly = true;
                m_typeColumn.Width = 150;

                m_treeGridView.Columns.AddRange(new DataGridViewColumn[] { m_fieldColumn, m_valueColumn, m_typeColumn });

                m_treeGridView.ImageList = m_imageListTreeGrid;
                m_treeGridView.ScrollBars = ScrollBars.Both;
           
                //m_treeGridView.BuildContextMenu();
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
        }

       

      

       

       
    }
}
