/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Reflection;
using System.Windows.Forms;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using OManager.BusinessLayer.Common;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Connection;
using OManager.DataLayer.Modal;
using OManager.DataLayer.Reflection;
using OManager.Properties;
using OME.AdvancedDataGridView;
using OME.Logging.Common;

namespace OManager.DataLayer.QueryParser
{
	public class RenderHierarchy
	{
		private ImageList m_imageListTreeGrid;

		private IObjectContainer container;

		public RenderHierarchy()
		{
			container = Db4oClient.Client;
		}

		public TreeGridView ReturnHierarchy(object currObj, string classname)
		{
			InitializeImageList();
			TreeGridView treegrid = InitializeTreeGridView();

			try
			{
				TreeGridNode rootNode = new TreeGridNode();
				treegrid.Nodes.Add(rootNode);

				IReflectClass rclass = DataLayerCommon.ReflectClassForName(classname);
				IType type = ResolveType(rclass);

				rootNode.Cells[0].Value = AppendIDTo(type.FullName, GetLocalID(currObj), type);
				rootNode.Cells[1].Value = ClassNameFor(currObj.ToString());
				SetFieldType(rootNode, type);
				rootNode.Tag = currObj;
				rootNode.Cells[1].ReadOnly = true;
				rootNode.Expand();
				rootNode.ImageIndex = 0;
				classname = DataLayerCommon.RemoveGFromClassName(classname);

				TraverseObjTree(ref rootNode, currObj, classname);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
			return treegrid;
		}

		private static string AppendIDTo(string prefix, long id, IType type)
		{
			string result = prefix;
			if (type.HasIdentity)
			{
				result = result + " (Object ID : " + id + " )";
			}

			return result; 
		}

		private void InitializeImageList()
		{
			try
			{
				m_imageListTreeGrid = new ImageList();
				m_imageListTreeGrid.Images.Add(Resource.treeview_class); //0 Class
				m_imageListTreeGrid.Images.Add(Resource.treeview_collection); //1 Collections 
				m_imageListTreeGrid.Images.Add(Resource.treeview_primitive); //2 Primitive
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public void TraverseObjTree(ref TreeGridNode rootNode, object currObj, string classname)
		{
			container = Db4oClient.Client;
			IReflectClass refClass = DataLayerCommon.ReflectClassForName(classname);
			if (refClass == null) 
				return;

			string type1 = refClass.ToString();
			if (!String.IsNullOrEmpty(type1))
			{
				try
				{
					IType type = Db4oClient.TypeResolver.Resolve(refClass);
					if (type.IsPrimitive)
					{
						TreeGridNode node = new TreeGridNode();
						rootNode.Nodes.Add(node);
						node.Cells[0].Value = ClassNameFor(container.Ext().Reflector().ForObject(currObj).GetName());
						node.Cells[1].Value = currObj.ToString();
						node.Cells[1].ReadOnly = false;
						SetFieldType(node, type);
						node.Tag = currObj;
						node.ImageIndex = 2;

						return;
					}
						
					IReflectField[] fieldArr = DataLayerCommon.GetDeclaredFieldsInHeirarchy(refClass);
					rootNode = TraverseFields(rootNode, currObj, fieldArr);
				}

				catch (Exception oEx)
				{
					LoggingHelper.HandleException(oEx);
				}
			}
		}

		private TreeGridNode TraverseFields(TreeGridNode parentNode, object parentObject, IEnumerable<IReflectField> fields)
		{
			if (fields == null)
				return parentNode;

			foreach (IReflectField field in fields)
			{
				IType fieldType = ResolveFieldType(field);
				if (fieldType == null)
					continue;

				if (fieldType.IsEditable) 
				{
					CreatePrimitiveNode(field.GetName(), field.Get(parentObject), ref parentNode, fieldType);
				}
				else if (fieldType.IsCollection)
				{
					RenderCollection(parentNode, parentObject, field);
				}
				else if (fieldType.IsArray)
				{
					RenderArray(parentNode, parentObject, field);
				}
				else
				{
					RenderSubObject(parentNode, parentObject, field);
				}
			}
			return parentNode;
		}

		private void RenderArray(TreeGridNode rootNode, object currObj, IReflectField field)
		{
			container = Db4oClient.Client;
			object obj = field.Get(currObj);
			
			container.Ext().Activate(obj, 2);

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

		private void RenderCollection(TreeGridNode rootNode, object currObj, IReflectField field)
		{
			container = Db4oClient.Client;
			object value = field.Get(currObj);
			container.Ext().Activate(value, 2);

			if (value != null)
			{
				ICollection coll = (ICollection) value;
				CreateCollectionNode(field, value, ref rootNode, coll.Count.ToString());
			}
			else
			{
				CreateCollectionNode(field, value, ref rootNode, "0");
			}
		}

		private void RenderSubObject(TreeGridNode parentNode, object parentObj, IReflectField field)
		{
			try
			{
				TreeGridNode objectNode = new TreeGridNode();
				parentNode.Nodes.Add(objectNode);
				object value = field.Get(parentObj);
				objectNode.Tag = value;

				IType fieldType = ResolveFieldType(field);
				objectNode.Cells[0].Value = AppendIDTo(field.GetName(), GetLocalID(value), fieldType);

				objectNode.Cells[1].Value = value != null ? value.ToString() : BusinessConstants.DB4OBJECTS_NULL;

				SetFieldType(objectNode, fieldType);

				container = Db4oClient.Client;
				container.Ext().Activate(value, 2);

				if (parentNode.Tag is DictionaryEntry && field.GetName() == BusinessConstants.DB4OBJECTS_KEY)
					objectNode.Cells[1].ReadOnly = true;
				else if (parentNode.Tag is DictionaryEntry && field.GetName() == BusinessConstants.DB4OBJECTS_VALUE)
					objectNode.Cells[1].ReadOnly = false;
				else if (field.Get(parentObj) == null)
					objectNode.Cells[1].ReadOnly = true;
				else
					objectNode.Cells[1].ReadOnly = true;

				objectNode.ImageIndex = 0; //class
				if (value != null)
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

		private static void SetFieldType(TreeGridNode node, IType type)
		{
			node.Cells[2].Value = type.DisplayName;
			node.Cells[2].Tag= type;
		}

		private static IType FieldTypeFor(TreeGridNode node)
		{
			return (IType) node.Cells[2].Tag;
		}

		private static string ClassNameFor(string type)
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

		public void CreateCollectionNode(IReflectField field, object ownerObject, ref TreeGridNode rootNode, string count)
		{
			try
			{
				TreeGridNode collNode = new TreeGridNode();

				rootNode.Nodes.Add(collNode);
				collNode.Cells[1].ReadOnly = true;
				collNode.Cells[0].Value = field.GetName() + " ( " + count + " items )";
				SetFieldType(collNode, ResolveFieldType(field));
				if (ownerObject != null)
				{
					collNode.Cells[1].Value = ownerObject.ToString();
					collNode.Tag = ownerObject;
				}
				else
				{
					collNode.Cells[1].Value = BusinessConstants.DB4OBJECTS_NULL;
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
				ExpandCollectionNode(node, (ICollection) node.Tag);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		private void ExpandCollectionNode(TreeGridNode parentNode, ICollection collection)
		{
			container = Db4oClient.Client;
			foreach (object item in collection)
			{
				if (item == null)
					continue;

				IType itemType = ResolveType(DataLayerCommon.ReflectClassFor(item));
				if (itemType.IsPrimitive)
					TraverseObjTree(ref parentNode, item, container.Ext().Reflector().ForObject(item).GetName());
				else
				{
					container.Ext().Activate(item, 1);
					TreeGridNode collNode = new TreeGridNode();
					parentNode.Nodes.Add(collNode);
					collNode.Cells[0].Value = AppendIDTo(item.ToString(), GetLocalID(item), itemType);

					SetFieldType(collNode, itemType);
					collNode.Cells[1].Value = ClassNameFor(item.ToString());
					collNode.Tag = item;
					collNode.ImageIndex = 0;
					collNode.Cells[1].ReadOnly = true;
					TraverseObjTree(ref collNode, item, itemType.FullName);
				}
			}
		}

		public void ExpandObjectNode(TreeGridNode node, bool activate)
		{
			try
			{
				object obj= node.Tag;
				if (obj == null)
					return;
				
				IReflectClass rclass = DataLayerCommon.ReflectClassFor(obj);
				if (rclass == null)
					return;

				string classname = rclass.GetName();
				if (classname != string.Empty)
				{
					TraverseObjTree(ref node, obj, classname);
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
				ExpandCollectionNode(node, (ICollection) node.Tag);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public void CreatePrimitiveNode(string fieldName, object value, ref TreeGridNode rootNode, IType type)
		{
			try
			{
				TreeGridNode primitiveNode = new TreeGridNode();
				rootNode.Nodes.Add(primitiveNode);
				primitiveNode.Cells[0].Value = fieldName;
				primitiveNode.Cells[1].ReadOnly = false;
				primitiveNode.Cells[1].Value = value != null ? value.ToString() : BusinessConstants.DB4OBJECTS_NULL;
				SetFieldType(primitiveNode, type);

				if (rootNode.Parent.Tag is IDictionary)
				{
					primitiveNode.Cells[1].ReadOnly = fieldName != BusinessConstants.DB4OBJECTS_VALUE1;
				}

				primitiveNode.Tag = value;
				primitiveNode.ImageIndex = 2; //Primitive
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		internal class ValueTypeChange
		{
			public ValueTypeChange(object targetObject)
			{
				TargetObject = targetObject;
				FieldPath = new List<FieldInfo>();
			}

			public object TargetObject;
			public List<FieldInfo> FieldPath;
		}

		/**
		 * Each node in "details view" holds a reference for its corresponding
		 * object in the object model. While this works fine with reference
		 * types, it fails miserably with nested value types. 
		 * 
		 * For more details see OMNUnitTest.RenderHierarchyTestCase.
		 */
		public static bool TryUpdateValueType(TreeGridNode node, object newValue)
		{
			if (node == null || node.Parent == null)
				return false;

			ValueTypeChange change = ValueTypeChangeFor(node.Parent, 0);
			if (change == null)
				return false;

			FieldInfo fieldInfo = FieldInfoFor(node);
			if (fieldInfo == null)
				return false;

			fieldInfo.SetValueDirect(TypedReference.MakeTypedReference(change.TargetObject, change.FieldPath.ToArray()), newValue);
			return true;
		}

		private static ValueTypeChange ValueTypeChangeFor(TreeGridNode node, int depth)
		{
			IType omnType = FieldTypeFor(node);
			if (omnType.IsCollection || omnType.IsArray)
				return null;

			Type type = Type.GetType(omnType.FullName);
			if (type == null)
				return null;

			if (type.IsValueType)
			{
				ValueTypeChange change = ValueTypeChangeFor(node.Parent, depth + 1);
				if (change != null) 
					change.FieldPath.Add(FieldInfoFor(node));

				return change;
			}

			return depth == 0 ? null : new ValueTypeChange(node.Tag);
		}


		private static FieldInfo FieldInfoFor(TreeGridNode node)
		{
			Type parentType = Type.GetType(FieldTypeFor(node.Parent).FullName);
			return (parentType != null)
			       	? parentType.GetField(FieldNameFor(node), BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Public)
			       	: null;
		}

		private static string FieldNameFor(TreeGridNode node)
		{
			return (string) node.Cells[0].Value;
		}

		public long GetLocalID(object obj)
		{
			ObjectDetails objDetails = new ObjectDetails(obj);
			return objDetails.GetLocalID();
		}

		public IType ResolveFieldType(IReflectField field)
		{
			IReflectClass type = field.GetFieldType();
			return type != null ? ResolveType(type) : null;
		}

		private static IType ResolveType(IReflectClass klass)
		{
			return Db4oClient.TypeResolver.Resolve(klass);
		}

		private TreeGridView InitializeTreeGridView()
		{
			try
			{
				//TreeGridView Initialization
				TreeGridView m_treeGridView = new TreeGridView();
				m_treeGridView.Size = new Size(530, 442);
				m_treeGridView.Location = new Point(2, 2);
				m_treeGridView.Name = BusinessConstants.DB4OBJECTS_TREEGRIDVIEW;
				m_treeGridView.RowHeadersVisible = false;
				m_treeGridView.ShowLines = true;
				m_treeGridView.Dock = DockStyle.Fill;
				m_treeGridView.Visible = true;

				//Column Intialization

				//Field Column
				TreeGridColumn m_fieldColumn = new TreeGridColumn();
				m_fieldColumn.DefaultNodeImage = null;
				m_fieldColumn.FillWeight = 386.9562F;
				m_fieldColumn.HeaderText = BusinessConstants.DB4OBJECTS_FIELD;
				m_fieldColumn.Name = BusinessConstants.DB4OBJECTS_FIELDCOLOUMN;
				m_fieldColumn.SortMode = DataGridViewColumnSortMode.NotSortable;
				m_fieldColumn.ReadOnly = true;
				m_fieldColumn.Width = 170;

				//Value Column
				TreeGridViewDateTimePickerColumn m_valueColumn = new TreeGridViewDateTimePickerColumn();
				m_valueColumn.FillWeight = 50F;
				m_valueColumn.HeaderText = BusinessConstants.DB4OBJECTS_VALUEFORGRID;
				m_valueColumn.Name = BusinessConstants.DB4OBJECTS_VALUECOLUMN;
				m_valueColumn.SortMode = DataGridViewColumnSortMode.NotSortable;
				m_valueColumn.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
				m_valueColumn.ReadOnly = false;

				//Type Column
				DataGridViewTextBoxColumn m_typeColumn = new DataGridViewTextBoxColumn();
				m_typeColumn.FillWeight = 50F;
				m_typeColumn.HeaderText = BusinessConstants.DB4OBJECTS_TYPE;
				m_typeColumn.Name = BusinessConstants.DB4OBJECTS_TYPECOLUMN;
				m_typeColumn.SortMode = DataGridViewColumnSortMode.NotSortable;
				m_typeColumn.ReadOnly = true;
				m_typeColumn.Width = 150;

				m_treeGridView.Columns.AddRange(new DataGridViewColumn[] {m_fieldColumn, m_valueColumn, m_typeColumn});

				m_treeGridView.ImageList = m_imageListTreeGrid;
				m_treeGridView.ScrollBars = ScrollBars.Both;

				return m_treeGridView;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

			return null;
		}
	}
}