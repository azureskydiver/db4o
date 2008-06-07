package com.db4o.omplus.ui.listeners.queryResult;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.db4o.omplus.datalayer.ReflectHelper;
import com.db4o.omplus.datalayer.queryresult.ObjectTreeNode;

public class ObjectTreeDragListener extends DragSourceAdapter{
	TreeViewer classTree;
//	TODO: Remove all TreeItems. 
//	final TreeItem[] dragSourceItem = new TreeItem[1];
	String data;
	
	 public ObjectTreeDragListener(TreeViewer viewer) {
	      this.classTree = viewer;
	   }
	 
	   /**
	    * Method declared on DragSourceListener
	    */
	   public void dragFinished(DragSourceEvent event) {
		   if (event.detail == DND.DROP_MOVE){
//			   dragSourceItem[0] = null;
			   data = null;
		   }
	   }
	   
	   /**
	    * Method declared on DragSourceListener
	    */
	   public void dragSetData(DragSourceEvent event) {
//		   event.data = dragSourceItem[0].getData();
		   event.data = data;
	   }
	   
	   /**
	    * Method declared on DragSourceListener
	    */
	   public void dragStart(DragSourceEvent event) {
		   TreeItem[] selection = classTree.getTree().getSelection();
		   event.doit = false;
		   if (selection.length > 0 && selection[0].getItemCount() == 0) {
			   System.out.print(selection[0].getData());
			   ObjectTreeNode node = (ObjectTreeNode)selection[0].getData();
			   String fieldName = node.getName();
			   int type = ReflectHelper.getFieldTypeClass(fieldName);
			   if(type > -1 && (type <= 10)){
				   event.doit = true;
//				   	dragSourceItem[0] = selection[0];
//					dragSourceItem[0].setData(fieldName);
				   data = fieldName;
			   }
		   }
	  }

}
