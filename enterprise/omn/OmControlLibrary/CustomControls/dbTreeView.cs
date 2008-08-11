using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.Collections;
using OManager.BusinessLayer.QueryManager;
using OME.Logging.Common;
using OME.Logging.Tracing;
using OManager.BusinessLayer.Login;

namespace OMControlLibrary.Common
{
	public partial class dbTreeView : TreeView
	{
		#region Member Variables
		private bool m_useInbuiltDragDrop = true;
		private ImageList imageListDrag;
		private TreeNode dragNode;
		private ImageList imageListTreeView;

		Hashtable m_hashtableAssmblyNodes = new Hashtable();
		Hashtable m_hashtableClassNodes = new Hashtable();

		private TreeNode m_PreviousTreeNode = new TreeNode();
		private TreeNode m_TreeNode = new TreeNode();
		TreeNode treenode = null;

		private ContextMenuStrip m_tvViewObjectsContextMenuStrip;
		private ContextMenuStrip m_tvAddtoQueryContextMenuStrip;
		private ContextMenuStrip m_tvFavFolderContexMenu;
		string folderName;
		//Events

		internal event EventHandler<DBContextItemClickedEventArg> OnContextMenuItemClicked;

		#endregion

		#region Properties
		/// <summary>
		/// Gets a value indicating whether drag drop in build feature is use to the tree view control. 
		/// [Default value is true].It is useful when you want to write your own drag drop event for the 
		/// tree view control. for the same value must be set to false.
		/// </summary>
		public bool UseInbuiltDragDrop
		{
			set { m_useInbuiltDragDrop = value; }
		}

		public Hashtable HashtableClassNodes
		{
			get { return m_hashtableClassNodes; }
			set { m_hashtableClassNodes = value; }
		}

		public Hashtable HashtableAssmblyNodes
		{
			get { return m_hashtableAssmblyNodes; }
			set { m_hashtableAssmblyNodes = value; }
		}
		#endregion

		#region Constructor

		public dbTreeView()
		{
			imageListDrag = new ImageList();
			imageListTreeView = new ImageList();
			this.AllowDrop = true;
			this.LabelEdit = true;

		}

		#endregion

		#region Event Handler
		/// <summary>
		/// DragEnter event.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		protected override void OnDragEnter(DragEventArgs e)
		{
			base.OnDragEnter(e);

			try
			{
				DragHelper.ImageList_DragEnter(this.Handle, e.X - this.Left,
										   e.Y - this.Top);

				e.Effect = DragDropEffects.Move;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// DrageOver event
		/// </summary>
		/// <param name="drgevent"></param>
		protected override void OnDragOver(DragEventArgs e)
		{
			try
			{
				base.OnDragOver(e);

				Point formP = this.PointToClient(new Point(e.X, e.Y));
				DragHelper.ImageList_DragMove(formP.X, formP.Y);

				e.Effect = DragDropEffects.Move;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		protected override void OnDragDrop(DragEventArgs e)
		{
			try
			{
				base.OnDragDrop(e);
				e.Effect = DragDropEffects.Move;
				Point pos = this.PointToClient(new Point(e.X, e.Y));
				TreeNode parentTreeNode = this.GetNodeAt(pos);
				if (parentTreeNode != null)
				{
					TreeNode dragNode = new TreeNode(Helper.FindRootNode(this.SelectedNode));
					dragNode.Tag = dragNode.Text;
					dragNode.Name = dragNode.Text;
					dragNode.ImageIndex =
						 dragNode.SelectedImageIndex = 1;
					if (dragNode.Tag != null && dragNode.Tag.ToString() != "Fav Folder")
					{
						//dragNode.Tag = this.SelectedNode.Text;
						AddDummyChildNode(dragNode);

						bool checkSameNode = false;
						for (int i = 0; i < parentTreeNode.Nodes.Count; i++)
						{
							TreeNode tNode = parentTreeNode.Nodes[i];
							if (tNode.Text == dragNode.Text)
							{
								checkSameNode = true;
								break;
							}
						}
						if (checkSameNode == false && parentTreeNode.Tag != null && parentTreeNode.Tag.ToString() == "Fav Folder")
						{
							dbInteraction dbI = new dbInteraction();
							parentTreeNode.Nodes.Add(dragNode);
							FavouriteFolder Fav = new FavouriteFolder(null, parentTreeNode.Text);
							Fav.FolderName = parentTreeNode.Text;
							if (parentTreeNode.Nodes.Count > 0)
							{
								List<string> lststr = new List<string>();
								foreach (TreeNode tnode in parentTreeNode.Nodes)
								{

									lststr.Add(tnode.Text);
								}
								Fav.ListClass = lststr;
							}

							dbI.SaveFavourite(dbI.GetCurrentRecentConnection().ConnParam, Fav);

						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		//private string  FindRootNode(TreeNode node)
		//{
		//    while (node.Parent != null && node.Parent.Tag != null && node.Parent.Tag.ToString() != "Fav Folder")
		//    {
		//        node = node.Parent; 
		//    }

		//    return node.Text; 
		//}
		protected override void OnAfterExpand(TreeViewEventArgs e)
		{
			base.OnAfterExpand(e);
			if (e.Node.Parent == null && e.Node.Tag != null && e.Node.Tag.ToString() == "Fav Folder")
			{
				e.Node.ImageIndex =
				  e.Node.SelectedImageIndex = 4;
			}
		}


		protected override void OnAfterCollapse(TreeViewEventArgs e)
		{
			base.OnAfterCollapse(e);
			if (e.Node.Parent == null && e.Node.Tag != null && e.Node.Tag.ToString() == "Fav Folder")
			{
				e.Node.ImageIndex =
				  e.Node.SelectedImageIndex = 5;
			}
		}


		/// <summary>
		/// ItemDrag Event
		/// </summary>
		/// <param name="e"></param>
		protected override void OnItemDrag(ItemDragEventArgs e)
		{


			try
			{
				string nodeName = null;
				TreeNode tNode = ((TreeNode)e.Item);
				tNode.TreeView.SelectedNode = tNode;
				((TreeNode)e.Item).TreeView.SelectedNode = tNode;
				if (tNode.Tag != null && tNode.Tag.ToString() == "Fav Folder" || tNode.Tag.ToString() == "Assembly View")
				{
					DoDragDrop(e.Item, DragDropEffects.None);
					return;
				}

				string typeOfObject = Helper.GetTypeOfObject(tNode.Tag.ToString());

				//check for arrays inside classes
				//this check helps in dragging classes
				if (tNode.Name.LastIndexOf(',') == -1 && tNode.Tag != null)
				{
					if (tNode.Parent.Tag.ToString().LastIndexOf(',') == -1)
						nodeName = tNode.Parent.Text.ToString();
					else
						nodeName = tNode.Parent.Tag.ToString();
				}
				else
				{
					nodeName = tNode.Name;
				}
				if (tNode.Nodes.Count == 0)
				{
					dbInteraction dbI = new dbInteraction();
					if (Helper.IsArrayOrCollection(typeOfObject)
						|| dbI.CheckForArray(nodeName, tNode.Text)
						|| dbI.CheckForCollection(nodeName, tNode.Text))
					{
						DoDragDrop(e.Item, DragDropEffects.None);
						return;
					}
				}



				//// Get drag node and select it
				this.dragNode = (TreeNode)e.Item;
				this.SelectedNode = this.dragNode;

				// Reset image list used for drag image
				this.imageListDrag.Images.Clear();

				//Check for the max image width 
				int imageWidth = this.dragNode.Bounds.Width + this.Indent;
				if (imageWidth > Constants.MAX_IMAGE_WIDTH)
					imageWidth = Constants.MAX_IMAGE_WIDTH;

				this.imageListDrag.ImageSize = new Size(imageWidth, this.dragNode.Bounds.Height);

				// Create new bitmap
				// This bitmap will contain the tree node image to be dragged


				Bitmap bmp = new Bitmap(imageWidth, this.dragNode.Bounds.Height);

				//// Get graphics from bitmap
				Graphics gfx = Graphics.FromImage(bmp);

				//// Draw node icon into the bitmap
				gfx.DrawImage(this.ImageList.Images[dragNode.ImageIndex], 0, 0);

				//// Draw node label into bitmap
				gfx.DrawString(this.dragNode.Text,
					this.Font,
					new SolidBrush(this.ForeColor),
					(float)this.Indent, 1.0f);

				//// Add bitmap to imagelist
				this.imageListDrag.Images.Add(bmp);

				//// Get mouse position in client coordinates
				Point p = this.PointToClient(Control.MousePosition);

				//// Compute delta between mouse position and node bounds
				int dx = p.X + this.Indent - this.dragNode.Bounds.Left;
				int dy = p.Y - this.dragNode.Bounds.Top;

				//// Begin dragging image
				if (DragHelper.ImageList_BeginDrag(this.imageListDrag.Handle, 0, dx, dy))
				{
					// Begin dragging
					this.DoDragDrop(bmp, DragDropEffects.Move);
					// End dragging image
					DragHelper.ImageList_EndDrag();
				}

				DoDragDrop(e.Item, DragDropEffects.Copy);
				base.OnItemDrag(e);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		protected override void OnMouseDown(MouseEventArgs e)
		{
			try
			{
				treenode = this.GetNodeAt(e.X, e.Y);
				if (treenode != null)
				{
					if (treenode.Tag != null && treenode.Tag.ToString() != "Assembly View")
					{
						base.OnMouseDown(e);
						if (treenode != null && e.Button == MouseButtons.Right)
						{
							this.SelectedNode = treenode;
							this.ContextMenuStrip = null;
							List<string> list = null;
							QueryBuilder queryBuilder = QueryBuilder.Instance;

							if (m_hashtableClassNodes.Contains(treenode.Name) ||
							m_hashtableAssmblyNodes.Contains(treenode.Name))
							{
								this.ContextMenuStrip = m_tvViewObjectsContextMenuStrip;
							}
							else
							{
								string className = string.Empty;
								string typeOfObject = string.Empty;

								if (treenode.Tag != null)
								{
									typeOfObject = Helper.GetTypeOfObject(treenode.Tag.ToString());

									if (Helper.IsPrimitive(typeOfObject))
									{
										if (!Helper.IsArrayOrCollection(typeOfObject))
											this.ContextMenuStrip = m_tvAddtoQueryContextMenuStrip;
									}
								}
							}
							if (treenode.Tag != null && treenode.Tag.ToString() == "Fav Folder")
							{
								list = queryBuilder.GetAllQueryGroups();
								//this.BuildContextMenu(list);
								this.BuildContextMenu(null, false, false);
								this.ContextMenuStrip = m_tvFavFolderContexMenu;
							}
							//else
							//{
							////else if (treenode.Tag.ToString() != "Fav Folder" && treenode.Parent.Tag.ToString() == "Fav Folder")
							////{
							//    //this.BuildContextMenu(null);
							//    //this.BuildContextMenu(null,false);
							//    this.ContextMenuStrip = m_tvViewObjectsContextMenuStrip;
							//}

						}

					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}




		protected override void OnKeyDown(KeyEventArgs e)
		{
			try
			{
				if (e.KeyCode == Keys.Delete)
				{
					dbInteraction dbI = new dbInteraction();
					FavouriteFolder Fav = null;
					if (this.SelectedNode.Tag != null && this.SelectedNode.Tag.ToString() == "Fav Folder")
					{


						Fav = new FavouriteFolder(null, this.SelectedNode.Text);
						dbI.UpdateFavourite(dbI.GetCurrentRecentConnection().ConnParam, Fav);
						this.Nodes.Remove(this.SelectedNode);


					}
					else if (this.SelectedNode.Parent != null && this.SelectedNode.Parent.Tag != null && this.SelectedNode.Parent.Tag.ToString() == "Fav Folder")
					{

						TreeNode tNode = this.SelectedNode;
						TreeNode parentNode = this.SelectedNode.Parent;


						if (parentNode.Nodes.Count > 0)
						{
							List<string> lststr = new List<string>();
							foreach (TreeNode tempNode in parentNode.Nodes)
							{
								lststr.Add(tempNode.Text);
							}
							Fav = new FavouriteFolder(lststr, tNode.Parent.Text);

						}
						dbI.SaveFavourite(dbI.GetCurrentRecentConnection().ConnParam, Fav);
						this.SelectedNode.Remove();
					}

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			//try
			//{
			//    if (this.SelectedNode != null && (e.KeyCode == Keys.Q && e.Control))
			//    {
			//        this.ContextMenuStrip = null;

			//        QueryBuilder queryBuilder = QueryBuilder.Instance;

			//        List<string> list = queryBuilder.GetAllQueryGroups();
			//        this.BuildContextMenu(list);

			//        if (m_hashtableClassNodes.Contains(this.SelectedNode.Name) ||
			//            m_hashtableAssmblyNodes.Contains(this.SelectedNode.Name))
			//        {
			//            this.ContextMenuStrip = m_tvViewObjectsContextMenuStrip;
			//        }
			//        else
			//        {
			//            string className = string.Empty;
			//            string typeOfObject = string.Empty;

			//            if (this.SelectedNode.Tag != null)
			//            {
			//                typeOfObject = Helper.GetTypeOfObject(this.SelectedNode.Tag.ToString());

			//                if (Helper.IsPrimitive(typeOfObject))
			//                {
			//                    if (!Helper.IsArrayOrCollection(typeOfObject))
			//                        this.ContextMenuStrip = m_tvAddtoQueryContextMenuStrip;
			//                }
			//            }
			//        }

			//        if (this.ContextMenuStrip != null)
			//            this.ContextMenuStrip.Show(this, new Point(this.SelectedNode.Bounds.X + this.SelectedNode.ToString().Length,
			//                this.SelectedNode.Bounds.Y + 5));
			//    }

			//    base.OnKeyDown(e);
			//}
			//catch (Exception oEx)
			//{
			//    LoggingHelper.ShowMessage(oEx);
			//}

		}

		protected void MainMenu_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
		{
			try
			{
				ToolStripItem tsItem = e.ClickedItem;

				if (tsItem.Tag != null)
				{
					DBContextItemClickedEventArg arg = new DBContextItemClickedEventArg(this.SelectedNode, e.ClickedItem.Tag);
					arg.Item = e.ClickedItem;

					if (OnContextMenuItemClicked != null)
						OnContextMenuItemClicked(sender, arg);
					if (ContextMenuStrip != null)
						this.ContextMenuStrip.Dispose();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		protected void SubMenu_DropDownItemClicked(object sender, ToolStripItemClickedEventArgs e)
		{
			try
			{
				DBContextItemClickedEventArg arg = new DBContextItemClickedEventArg(this.SelectedNode, e.ClickedItem.Tag);

				arg.Item = e.ClickedItem;
				if (OnContextMenuItemClicked != null)
					OnContextMenuItemClicked(sender, arg);

				this.ContextMenuStrip.Dispose();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Helper Methods

		public void SetTreeViewImages()
		{
			try
			{
				imageListTreeView.Images.Add(dbImages.TreeViewAssembly); //0 Assembly
				imageListTreeView.Images.Add(dbImages.TreeViewClass); //1 Classes
				imageListTreeView.Images.Add(dbImages.TreeViewPrimitive); //2 Primitive
				imageListTreeView.Images.Add(dbImages.TreeViewCollection); //3 Primitive
				imageListTreeView.Images.Add(dbImages.openFolder);// 4 open Folder
				imageListTreeView.Images.Add(dbImages.closedFolder);// 5 closed Folder

				this.ImageList = imageListTreeView;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		public void AddFavouritFolderFromDatabase()
		{
			try
			{
				dbInteraction dbI = new dbInteraction();
				List<FavouriteFolder> lstfavFolder = dbI.GetFavourites(dbI.GetCurrentRecentConnection().ConnParam);

				if (lstfavFolder != null)
				{
					foreach (FavouriteFolder fav in lstfavFolder)
					{
						if (fav != null)
						{
							TreeNode ParentFolder = new TreeNode(fav.FolderName);
							ParentFolder.Name = fav.FolderName;
							ParentFolder.Tag = "Fav Folder";
							if (fav.ListClass != null)
							{
								foreach (string str in fav.ListClass)
								{
									TreeNode child = new TreeNode(str);
									string strValue;
									int index = str.LastIndexOf(',');
									if (index == -1)
									{
										strValue = str;
									}
									else
									{
										strValue = str.Substring(0, index);
									}
									child.Tag = strValue;
									child.Name = str;
									child.ImageIndex =
											child.SelectedImageIndex = 1;
									ParentFolder.Nodes.Add(child);

									AddDummyChildNode(child);
								}
							}
							ParentFolder.ImageIndex =
							   ParentFolder.SelectedImageIndex = 5;
							this.Nodes.Add(ParentFolder);

						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		public void AddTreeNode(Hashtable list, TreeNode treenodeparent)
		{
			string typeofObject = string.Empty;
			try
			{
				if (list != null)
				{
					IDictionaryEnumerator enumerator =
						 list.GetEnumerator();

					TreeNode treeNodeNew = null;
					bool isPrimitiveType = false;

					if (treenodeparent == null)
						m_hashtableClassNodes.Clear();

					this.BeginUpdate();

					while (enumerator.MoveNext())
					{

						string nodevalue = string.Empty;
						string nodetype = string.Empty;

						Application.DoEvents();

						nodevalue = enumerator.Key.ToString();
						nodetype = enumerator.Value.ToString();

						if (!string.IsNullOrEmpty(nodevalue))
							treeNodeNew = new TreeNode(nodevalue);

						treeNodeNew.Name = nodevalue;
						treeNodeNew.Tag = nodetype;

						if (treenodeparent == null)
						{
							treeNodeNew.ImageIndex =
							treeNodeNew.SelectedImageIndex = 1; //Classes

							if (!this.HashtableClassNodes.ContainsKey(treeNodeNew.Name))
								this.HashtableClassNodes.Add(treeNodeNew.Name, treeNodeNew);

							this.Nodes.Add(treeNodeNew);
							AddDummyChildNode(treeNodeNew);
							continue;
						}
						else
						{
							typeofObject = Helper.GetTypeOfObject(nodetype);
							treeNodeNew.ImageIndex =
							   treeNodeNew.SelectedImageIndex = SetImageIndex(typeofObject);

							isPrimitiveType = Helper.IsPrimitive(typeofObject);
							treenodeparent.Nodes.Add(treeNodeNew);
						}

						if (!isPrimitiveType)
						{
							string param = string.Empty;

							if (treeNodeNew.Name.LastIndexOf(",") == -1)
								param = treeNodeNew.Tag.ToString();
							else
								param = treeNodeNew.Name;

							string className = string.Empty;

							if (treenodeparent.Name.LastIndexOf(',') > 0)
							{
								className = treenodeparent.Name;
							}
							else
							{
								className = treenodeparent.Tag.ToString();
							}

							bool collection = Helper.DbInteraction.CheckForCollection(className, nodevalue);
							bool isarray = Helper.DbInteraction.CheckForArray(className, nodevalue);
							if (Helper.DbInteraction.GetFieldCount(param) > 0)
							{
								if (!collection || !isarray)
								{
									AddDummyChildNode(treeNodeNew);
									treeNodeNew.ImageIndex =
											treeNodeNew.SelectedImageIndex = 1; //Classes;
								}
							}
							else
							{
								if (collection || isarray)
								{
									treeNodeNew.ImageIndex =
											treeNodeNew.SelectedImageIndex = 3; //Classes;
								}
							}
						}
					}

				}
			}

			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				//this.Sort();
				this.EndUpdate();
			}
		}

		private int SetImageIndex(string type)
		{
			int imageIndex = 0;

			try
			{
				if (Helper.IsPrimitive(type))
				{
					imageIndex = 2;
				}
				else if (Helper.IsArrayOrCollection(type))
				{
					imageIndex = 3;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return imageIndex;
		}

		/// <summary>
		/// This functions adds the dummy node under the specified node
		/// </summary>
		/// <param name="treenodeParent">Node 
		/// under which the dummy node is to added</param>
		private static void AddDummyChildNode(TreeNode treenodeParent)
		{
			try
			{
				//Code to calculate number of child removed,
				//instead, when the group is expanded, childs are retrieved.
				if (treenodeParent != null)
				{
					TreeNode treenodeDummyChildNode =
							new TreeNode(Constants.DUMMY_NODE_TEXT);
					treenodeDummyChildNode.Name = Constants.DUMMY_NODE_TEXT;
					treenodeParent.Nodes.Add(treenodeDummyChildNode);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public void PopulateAssemblyTreeView(Hashtable list)
		{
			try
			{
				IDictionaryEnumerator enumerator =
					 list.GetEnumerator();

				TreeNode treeNodeNew = null;

				string nodevalue = string.Empty;
				string nodetype = string.Empty;

				this.BeginUpdate();

				this.Nodes.Clear();
				HashtableAssmblyNodes.Clear();

				while (enumerator.MoveNext())
				{

					nodevalue = enumerator.Key.ToString();
					List<string> classes = (List<string>)enumerator.Value;

					if (!string.IsNullOrEmpty(nodevalue))
						treeNodeNew = new TreeNode(nodevalue);
					else
						return;

					treeNodeNew.Name = nodevalue;
					treeNodeNew.Tag = "Assembly View";

					if (classes.Count > 0)
					{
						for (int i = 0; i < classes.Count; i++)
						{
							TreeNode newClassesTreeNodes = new TreeNode(classes[i].ToString());
							newClassesTreeNodes.Name = classes[i].ToString();
							newClassesTreeNodes.Tag = classes[i].ToString();
							newClassesTreeNodes.ImageIndex =
								newClassesTreeNodes.SelectedImageIndex = 1; //Classes

							if (!this.HashtableAssmblyNodes.ContainsKey(newClassesTreeNodes.Name))
								this.HashtableAssmblyNodes.Add(newClassesTreeNodes.Name, newClassesTreeNodes);

							treeNodeNew.Nodes.Add(newClassesTreeNodes);
							AddDummyChildNode(newClassesTreeNodes);
						}
					}

					treeNodeNew.ImageIndex = treeNodeNew.SelectedImageIndex = 0; //Assembly

					if (!m_hashtableAssmblyNodes.ContainsKey(treeNodeNew.Name))
						m_hashtableAssmblyNodes.Add(treeNodeNew.Name, treeNodeNew);

					this.Nodes.Add(treeNodeNew);
					treeNodeNew.Expand();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				this.EndUpdate();
			}

		}
		public void AddFavoriteFolder()
		{
			TreeNode treeNodeNew = new TreeNode();
			List<string> favFolderList = new List<string>();
			int count = 0;
			try
			{
				for (int i = 0; i < this.Nodes.Count; i++)
				{
					TreeNode tNode = this.Nodes[i];
					if (tNode.Tag != null && tNode.Tag.ToString() == "Fav Folder")
					{
						count++;
						favFolderList.Add(tNode.Text);
					}
				}
				if (count > 0)
				{
					//dbInteraction dbI = new dbInteraction();
					//dbI.GetFolderfromDatabaseByFoldername(dbI.GetCurrentRecentConnection().ConnParam, "New Folder " + count.ToString());   
					bool checkUnique;
					treeNodeNew.Text = "New Folder " + count.ToString();
					while (true)
					{

						checkUnique = CheckUniqueFolderName(treeNodeNew, favFolderList, count);
						if (checkUnique == false)
						{
							count++;
							treeNodeNew.Text = "New Folder " + count.ToString();
						}
						else
						{
							break;
						}
					}
				}
				else
				{
					treeNodeNew.Text = "New Folder";
				}
				treeNodeNew.Tag = "Fav Folder";
				treeNodeNew.ImageIndex =
					treeNodeNew.SelectedImageIndex = 5;

				this.Nodes.Insert(0, treeNodeNew);
				this.SelectedNode = treeNodeNew;
				this.LabelEdit = true;
				this.SelectedNode.BeginEdit();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		private static bool CheckUniqueFolderName(TreeNode treeNodeNew, List<string> favFolderList, int count)
		{
			try
			{
				foreach (string str in favFolderList)
				{
					if (str == treeNodeNew.Text)
					{
						return false;
					}

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			return true;
		}
		protected override void OnBeforeLabelEdit(NodeLabelEditEventArgs e)
		{
			try
			{
				base.OnBeforeLabelEdit(e);
				folderName = this.SelectedNode.Text;
				if (e.Node.Tag != null && e.Node.Tag.ToString() != "Fav Folder")
				{
					this.LabelEdit = false;

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		protected override void OnAfterLabelEdit(NodeLabelEditEventArgs e)
		{
			try
			{
				base.OnAfterLabelEdit(e);
				if (!string.IsNullOrEmpty(e.Label))
				{
					bool checkSameNode = false;
					for (int i = 0; i < this.Nodes.Count; i++)
					{
						TreeNode tNode = this.Nodes[i];
						if (tNode.Text == e.Label)
						{
							checkSameNode = true;
							break;
						}
					}
					if (checkSameNode == false)
					{
						e.Node.Text = e.Label;
					}
					else
					{
						MessageBox.Show("The Foldername already exist, Please use some other name.", Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK, MessageBoxIcon.Error);
						e.CancelEdit = true;

					}
				}
				else if (e.Label == string.Empty)
				{
					MessageBox.Show("The Foldername cannot be empty.", Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK, MessageBoxIcon.Error);
					e.CancelEdit = true;

				}
				if (e.Node.Text != folderName)
				{
					dbInteraction dbI = new dbInteraction();
					FavouriteFolder oldfav = new FavouriteFolder(null, folderName);
					FavouriteFolder newFav = new FavouriteFolder(null, e.Node.Text);
					dbI.RenameFolderInDatabase(dbI.GetCurrentRecentConnection().ConnParam, oldfav, newFav);
				}
				else
				{
					dbInteraction dbI = new dbInteraction();
					FavouriteFolder newFav = new FavouriteFolder(null, e.Node.Text);
					dbI.SaveFavourite(dbI.GetCurrentRecentConnection().ConnParam, newFav);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}


		public void FindTreeNodesClasses(Hashtable list, TreeNode treenodeparent, string strToFind)
		{
			string typeofObject = string.Empty;
			try
			{
				IDictionaryEnumerator enumerator =
					 list.GetEnumerator();

				TreeNode treeNodeNew = null;
				bool isPrimitiveType = false;

				if (this.Nodes.Count > 0)
					this.Nodes.Clear();

				m_hashtableClassNodes.Clear();
				this.BeginUpdate();

				while (enumerator.MoveNext())
				{

					string nodevalue = string.Empty;
					string nodetype = string.Empty;

					nodevalue = enumerator.Key.ToString();
					nodetype = enumerator.Value.ToString();

					if (nodevalue.ToLower().Contains(strToFind))
					{
						if (!string.IsNullOrEmpty(nodevalue))
							treeNodeNew = new TreeNode(nodevalue);

						treeNodeNew.Name = nodevalue;
						treeNodeNew.Tag = nodetype;

						if (treenodeparent == null)
						{
							treeNodeNew.ImageIndex =
							treeNodeNew.SelectedImageIndex = 1; //Classes

							if (!m_hashtableClassNodes.ContainsKey(treeNodeNew.Name))
							{
								m_hashtableClassNodes.Add(treeNodeNew.Name, treeNodeNew);
							}
							this.Nodes.Add(treeNodeNew);

							AddDummyChildNode(treeNodeNew);
							continue;
						}
						else
						{
							typeofObject = Helper.GetTypeOfObject(nodetype);
							treeNodeNew.ImageIndex =
							   treeNodeNew.SelectedImageIndex = SetImageIndex(typeofObject);

							isPrimitiveType = Helper.IsPrimitive(typeofObject);
							treenodeparent.Nodes.Add(treeNodeNew);
						}

						if (!isPrimitiveType)
						{
							string param = string.Empty;

							if (treeNodeNew.Name.IndexOf(",") == -1)
								param = treeNodeNew.Tag.ToString();
							else
								param = treeNodeNew.Name;

							string className = string.Empty;

							if (treenodeparent.Name.LastIndexOf(',') > 0)
							{
								className = treenodeparent.Name;
							}
							else
							{
								className = treenodeparent.Tag.ToString();
							}

							if (Helper.DbInteraction.GetFieldCount(param) > 0)
							{
								bool collection = Helper.DbInteraction.CheckForCollection(className, nodevalue);
								bool isarray = Helper.DbInteraction.CheckForArray(className, nodevalue);
								if (!collection || !isarray)
								{
									AddDummyChildNode(treeNodeNew);
									treeNodeNew.ImageIndex =
											treeNodeNew.SelectedImageIndex = 1; //Classes;
								}
							}
							else
							{
								bool collection = Helper.DbInteraction.CheckForCollection(className, nodevalue);
								bool isarray = Helper.DbInteraction.CheckForArray(className, nodevalue);
								if (collection || isarray)
								{
									treeNodeNew.ImageIndex =
											treeNodeNew.SelectedImageIndex = 3; //Classes;
								}
							}
						}
					}
				}
			}

			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				//this.Sort();
				this.EndUpdate();
			}
		}

		public void FindTreeNodesAssemblyView(Hashtable list, string strToFind)
		{
			TreeNode treeNodeNew = null;

			string nodevalue = string.Empty;
			string nodetype = string.Empty;

			try
			{
				IDictionaryEnumerator enumerator =
					 list.GetEnumerator();

				this.BeginUpdate();
				this.Nodes.Clear();
				m_hashtableAssmblyNodes.Clear();

				while (enumerator.MoveNext())
				{
					nodevalue = enumerator.Key.ToString();
					List<string> classes = (List<string>)enumerator.Value;

					if (!string.IsNullOrEmpty(nodevalue))
						treeNodeNew = new TreeNode(nodevalue);
					else
						return;

					treeNodeNew.Name = nodevalue;
					treeNodeNew.Tag = "Assembly View";
					if (classes.Count > 0)
					{
						for (int i = 0; i < classes.Count; i++)
						{
							TreeNode newClassesTreeNodes = new TreeNode(classes[i].ToString());
							newClassesTreeNodes.Name = classes[i].ToString();
							if (newClassesTreeNodes.Name.ToLower().Contains(strToFind))
							{
								newClassesTreeNodes.Tag = classes[i].ToString();
								newClassesTreeNodes.ImageIndex =
									newClassesTreeNodes.SelectedImageIndex = 1; //Classes

								if (!m_hashtableAssmblyNodes.ContainsKey(newClassesTreeNodes.Name))
									m_hashtableAssmblyNodes.Add(newClassesTreeNodes.Name, newClassesTreeNodes);

								treeNodeNew.Nodes.Add(newClassesTreeNodes);
								AddDummyChildNode(newClassesTreeNodes);
							}
						}
					}

					treeNodeNew.ImageIndex = treeNodeNew.SelectedImageIndex = 0; //Assembly
					this.Nodes.Add(treeNodeNew);
					treeNodeNew.Expand();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				this.EndUpdate();
			}

		}

		protected override void OnAfterSelect(TreeViewEventArgs e)
		{
			base.OnAfterSelect(e);
		}


		public void FindNSelectNode(TreeNode Node, string className, dbTreeView dbtree)
		{
			try
			{
				string classNameWithoutAssembly = className;
				int index = classNameWithoutAssembly.LastIndexOf(',');
				string strClassName = classNameWithoutAssembly.Remove(index);
				//bool foundNode = false;
				if (Node != null && Node.Tag != null)
				{

					if (Node.Tag.Equals(className) || Node.Tag.Equals(strClassName))
					{
						dbtree.SelectedNode = Node;
						return;
					}
					else
					{
						if (Node.Parent != null
							&& Node.Parent.Tag != null)
						{
							if (Node.Nodes.Count >= 1)
							{
								for (int i = 0; i < Node.Nodes.Count; i++)
								{
									if (Node.Nodes[i].Nodes.Count > 0)
									{

										if (Node.Nodes[i].Tag.Equals(className) || Node.Nodes[i].Tag.Equals(strClassName))
										{
											dbtree.SelectedNode = Node.Nodes[i];
											//foundNode = true;
											return;

										}
										else
										{
											FindNSelectNode(Node.Nodes[i], className, dbtree);
										}
									}
								}
							}
						}
						if (Node.Tag.ToString() != "Assembly View")
							Node = Node.NextNode;
						else
						{
							if (Node.Nodes != null && Node.Nodes.Count > 0)
								Node = Node.Nodes[0];

						}
						FindNSelectNode(Node, className, dbtree);
					}
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Heightlights the selected node.
		/// </summary>
		/// <param name="tagName">Tag assigned to a treenode</param>
		public void UpdateTreeNodeSelection(TreeNode selectedNode, bool isAssemblyView)
		{

			string selectedNodeName = string.Empty;
			TreeNode parentNode = new TreeNode();
			Hashtable m_htnodes = new Hashtable();
			try
			{
				if (selectedNode == null)
					return;

				//Check whether tree node with given tag exists.
				if (isAssemblyView)
					m_htnodes = this.HashtableAssmblyNodes;
				else
					m_htnodes = this.HashtableClassNodes;


				parentNode = selectedNode.Parent;
				selectedNodeName = selectedNode.Text;
				while (parentNode != null && parentNode.Tag != null
					 && parentNode.Tag.ToString() != "Assembly View" && parentNode.Tag.ToString() != "Fav Folder")
				{
					selectedNodeName = parentNode.Name;

					parentNode = parentNode.Parent;
				}
				UnHighlightAllFavFolders();
				if (parentNode != null && parentNode.Tag != null && parentNode.Tag.ToString() != "Assembly View")
				{
					HighlightFavFolder(selectedNodeName, parentNode);
				}
				else
				{
					if (m_htnodes.Contains(selectedNodeName))
					{
						//get the treenode to be selected.
						m_TreeNode = m_htnodes[selectedNodeName] as TreeNode;
						if (m_TreeNode != null)
						{
							m_TreeNode.Text.Trim();
							//Select the node.
							if (selectedNode.Name.LastIndexOf(",") != -1)
								this.SelectedNode = m_TreeNode;
							else
								this.SelectedNode = selectedNode;

							m_TreeNode.Text += "            ";
							Font fontTree = new Font(this.Font.Name, this.Font.Size, FontStyle.Bold);
							m_TreeNode.NodeFont = fontTree;
							m_TreeNode.Text = m_TreeNode.Text.Trim();
						}
						//Check if previously selected treenode is not null.
						if (m_PreviousTreeNode != null && m_PreviousTreeNode != m_TreeNode)
						{
							//Set previously selected treenode as regular font.
							Font preFont = new Font(this.Font.Name, this.Font.Size, FontStyle.Regular);
							m_PreviousTreeNode.NodeFont = preFont;
							m_PreviousTreeNode = m_TreeNode;
							m_PreviousTreeNode.Text = m_PreviousTreeNode.Text.Trim();
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void UnHighlightAllFavFolders()
		{
			try
			{
				foreach (TreeNode node in this.Nodes)
				{
					if (node.Tag != null && node.Tag.ToString() == "Fav Folder")
					{
						foreach (TreeNode tNode in node.Nodes)
						{
							Font preFont = new Font(this.Font.Name, this.Font.Size, FontStyle.Regular);
							tNode.NodeFont = preFont;

						}

					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void HighlightFavFolder(string selectedNodeName, TreeNode parentNode)
		{
			try
			{
				if (parentNode.Tag != null && parentNode.Tag.ToString() == "Fav Folder")
				{
					string FavFol = parentNode.Text;
					foreach (TreeNode node in this.Nodes)
					{
						if (node.Text == FavFol)
						{
							foreach (TreeNode tNode in node.Nodes)
							{
								if (tNode.Text == selectedNodeName)
								{
									Font fontTree = new Font(this.Font.Name, this.Font.Size, FontStyle.Bold);
									tNode.NodeFont = fontTree;
									tNode.Text = tNode.Text.Trim();

								}
								else
								{
									Font preFont = new Font(this.Font.Name, this.Font.Size, FontStyle.Regular);
									tNode.NodeFont = preFont;
								}
							}


						}
						else
						{
							Font preFont = new Font(this.Font.Name, this.Font.Size, FontStyle.Regular);
							node.NodeFont = preFont;
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public void BuildContextMenu(List<string> contextmenulist, bool FavChild, bool showDeleteClass)
		{
			string menuName = string.Empty;
			string menuText = string.Empty;

			ToolStripMenuItem objSubMenu;
			ToolStripMenuItem objMainMenu;
			ToolStripMenuItem objMainMenu1;
			try
			{
				if (FavChild == false)
				{
					m_tvFavFolderContexMenu = new ContextMenuStrip();
					//m_tvAddtoQueryContextMenuStrip = new ContextMenuStrip();


					m_tvFavFolderContexMenu.Name = this.Name;
					objMainMenu = new ToolStripMenuItem("Rename");
					objMainMenu.Name = "Rename";
					objMainMenu.Tag = "Rename";
					m_tvFavFolderContexMenu.Items.Add(objMainMenu);

					objMainMenu1 = new ToolStripMenuItem("Delete Folder");
					objMainMenu1.Name = "Delete Folder";
					objMainMenu1.Tag = "Delete Folder";
					m_tvFavFolderContexMenu.Items.Add(objMainMenu1);
					m_tvFavFolderContexMenu.ItemClicked += new ToolStripItemClickedEventHandler(MainMenu_ItemClicked);
					m_tvFavFolderContexMenu.Opening += new CancelEventHandler(ContextMenuStrip_Opening);

					//TreeView_OnContextMenuItemClicked
				}
				else
				{
					m_tvViewObjectsContextMenuStrip = new ContextMenuStrip();
					m_tvAddtoQueryContextMenuStrip = new ContextMenuStrip();
					m_tvViewObjectsContextMenuStrip.Name = this.Name;

					if (contextmenulist != null)
					{
						menuName = Common.Constants.CONTEXT_MENU_ADD_TO_ATTRIBUTE;
						objMainMenu = new ToolStripMenuItem(Helper.GetResourceString(Common.Constants.CONTEXT_MENU_ADD_TO_ATTRIBUTE));
						objMainMenu.Name = menuName;
						objMainMenu.Tag = (object)menuName;
						//objMainMenu.Enabled = !(Helper.HashTableBaseClass.Count > 0 && !Helper.HashTableBaseClass.Contains(selectedNodeName));

						m_tvAddtoQueryContextMenuStrip.Items.Add(objMainMenu);

						objMainMenu = new ToolStripMenuItem(Helper.GetResourceString(Common.Constants.CONTEXT_MENU_ADD_TO_QUERY));
						objMainMenu.Name = Common.Constants.CONTEXT_MENU_ADD_TO_QUERY;
						objMainMenu.Tag = null;
						//objMainMenu.Enabled = !(Helper.HashTableBaseClass.Count > 0 && !Helper.HashTableBaseClass.Contains(selectedNodeName));

						for (int i = 0; i < contextmenulist.Count; i++)
						{
							objSubMenu = new ToolStripMenuItem();
							objSubMenu.Text = contextmenulist[i].ToString();
							objSubMenu.Name = Common.Constants.CONTEXT_MENU_EXPRESSION_GROUP + i.ToString();
							objSubMenu.Tag = Common.Constants.CONTEXT_MENU_EXPRESSION_GROUP;
							objMainMenu.DropDownItems.Add(objSubMenu);

						}
						m_tvAddtoQueryContextMenuStrip.Items.Add(objMainMenu);


						objMainMenu.DropDownItemClicked += new ToolStripItemClickedEventHandler(SubMenu_DropDownItemClicked);
						m_tvAddtoQueryContextMenuStrip.ItemClicked += new ToolStripItemClickedEventHandler(MainMenu_ItemClicked);
						m_tvAddtoQueryContextMenuStrip.Opening += new CancelEventHandler(ContextMenuStrip_Opening);
					}
					else
					{
						objMainMenu = new ToolStripMenuItem(Helper.GetResourceString(Common.Constants.CONTEXT_MENU_SHOW_ALL_OBJECTS));
						objMainMenu.Name = Common.Constants.CONTEXT_MENU_SHOW_ALL_OBJECTS;
						objMainMenu.Tag = Common.Constants.CONTEXT_MENU_SHOW_ALL_OBJECTS;
						m_tvViewObjectsContextMenuStrip.Items.Add(objMainMenu);
						if (showDeleteClass)
						{
							objMainMenu1 = new ToolStripMenuItem("Delete Class");
							objMainMenu1.Name = "Delete Class";
							objMainMenu1.Tag = "Delete Class";
							m_tvViewObjectsContextMenuStrip.Items.Add(objMainMenu1);

						}
						m_tvViewObjectsContextMenuStrip.ItemClicked += new ToolStripItemClickedEventHandler(MainMenu_ItemClicked);
						m_tvViewObjectsContextMenuStrip.Opening += new CancelEventHandler(ContextMenuStrip_Opening);
					}

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void ContextMenuStrip_Opening(object sender, CancelEventArgs e)
		{
			try
			{
				if (treenode == null || treenode.Tag.ToString() == "Assembly View")
				{
					e.Cancel = true;
					return;
				}

				if (treenode.Tag != null)
				{
					string selectedNodeName = string.Empty;

					if (this.SelectedNode != null)
					{
						if (this.SelectedNode.Name.LastIndexOf(",") == -1)
						{
							TreeNode parentNode = new TreeNode();

							parentNode = this.SelectedNode.Parent;

							while (parentNode != null && parentNode.Tag != null && parentNode.Tag.ToString() != "Fav Folder" && parentNode.Tag.ToString() != "Assembly View")
							{
								selectedNodeName = parentNode.Name;
								parentNode = parentNode.Parent;
							}

							if (Helper.HashTableBaseClass.Count > 0 && !Helper.HashTableBaseClass.Contains(selectedNodeName))
								e.Cancel = true;
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion
	}


	/// <summary>
	/// DBContextItemClickedEventArg : For handling the contextmenu click
	/// </summary>
	public class DBContextItemClickedEventArg : System.EventArgs
	{
		private object m_data = null;
		private object m_tag = null;
		private object m_item = null;

		public object Item
		{
			get { return m_item; }
			set { m_item = value; }
		}

		public object Tag
		{
			get { return m_tag; }
			set { m_tag = value; }
		}

		public object Data
		{
			get { return m_data; }
			set { m_data = value; }
		}

		public DBContextItemClickedEventArg(object data, object tag)
		{
			m_data = data;
			m_tag = tag;
		}

		public DBContextItemClickedEventArg()
		{
		}
	}

	/// <summary>
	/// Class provides the effect of draging a item of treeview
	/// </summary>
	public class DragHelper
	{
		[DllImport("comctl32.dll")]
		public static extern bool InitCommonControls();

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern bool ImageList_BeginDrag(IntPtr himlTrack, int
			iTrack, int dxHotspot, int dyHotspot);

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern bool ImageList_DragMove(int x, int y);

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern void ImageList_EndDrag();

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern bool ImageList_DragEnter(IntPtr hwndLock, int x, int y);

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern bool ImageList_DragLeave(IntPtr hwndLock);

		[DllImport("comctl32.dll", CharSet = CharSet.Auto)]
		public static extern bool ImageList_DragShowNolock(bool fShow);

		static DragHelper()
		{
			InitCommonControls();
		}
	}
}
