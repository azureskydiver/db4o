/*
 * Created on Jan 14, 2005
 */
package com.db4o.browser.gui.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.db4o.browser.gui.standalone.Model;
import com.db4o.browser.gui.tree.ClassNode;
import com.db4o.browser.gui.tree.ITreeNode;
import com.db4o.ext.StoredClass;
import com.swtworkbench.community.xswt.XSWT;

/**
 * Class DbBrowserPane.  A SWT control that can be used as the basis for a
 * database browser.  This implementation defines the UI in an layout.xswt
 * file.
 * 
 * @author djo
 */
public class DbBrowserPane extends Composite {
	
	/**
     * Construct a DbBrowserPane object.
     * 
     * The standard SWT constructor.  Note that although this class extends
     * Composite, it does not make sense to set a layout manager on it.
     * 
	 * @param parent
	 * @param style
	 */
	public DbBrowserPane(Composite parent, int style) {
		super(parent, style);
        parent.setLayout(new FillLayout());
        setLayout(new FillLayout());
        contents = XSWT.createl(this, "layout.xswt", getClass());
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                closeModel();
            }
        });
        getObjectTree().addListener(SWT.Expand, expandListener);
        getObjectTree().addFocusListener(focusListener);
	}
    
    /**
     * Set the input to a new Model object.
     * <p>
     * This method automatically closes any existing open model in this
     * browser pane.
     * 
     * @param _model
     */
    public void setInput(Model _model) {
        // Dispose any existing items
        TreeItem[] items = getObjectTree().getItems();
        for (int i = 0; i < items.length; i++) {
			items[i].dispose();
		}
        closeModel();
        
        // Now load the new items
        model = _model;
        populateTree(model.storedClasses());
    }
    

    /**
     * If a model is open, close it
     */
    private void closeModel() {
        if (model != null) {
            model.close();
            model = null;
        }
    }

    private Model model = null;
    
    private Map contents = null;
    
    /**
     * Returns the Path Label
     * 
     * @return the Path label from the layout
     */
    public Label getPath() {
        return (Label) contents.get("Path");
    }
    
    /**
     * Returns the Search text box
     * 
     * @return the search text box
     */
    public Text getSearch() {
        return (Text) contents.get("Search");
    }
    
    /**
     * Returns the "Go" button next to the search text box
     * 
     * @return the search button
     */
    public Button getSearchButton() {
        return (Button) contents.get("SearchButton");
    }
    
    /**
     * Returns the object tree
     * 
     * @return the object tree
     */
    public Tree getObjectTree() {
        return (Tree) contents.get("ObjectTree");
    }
    
    /**
     * Returns the detail view composite
     * 
     * @return the detail view composite
     */
    public Composite getDetailComposite() {
        return (Composite) contents.get("DetailComposite");
    }
    
    // ---------------------------------------------------------------
    // Browser pane controller methods here
    // ---------------------------------------------------------------

    public void populateTree(StoredClass[] contents) {
        Tree theTree = getObjectTree();
        
        for (int i=0; i < contents.length; ++i) {
            ITreeNode node = new ClassNode(contents[i], model);
            initTreeItem(new TreeItem(theTree, SWT.NULL), node);
        }
    }
    
    private Listener expandListener = new Listener() {
        public void handleEvent (final Event event) {
            // Delete the dummy item if it exists
            final TreeItem treeItem = (TreeItem) event.item;
            TreeItem [] childrenItems = treeItem.getItems ();
            for (int i= 0; i<childrenItems.length; i++) {
                if (childrenItems [i].getData () != null) return;
                childrenItems [i].dispose ();
            }
            
            // Add the real children if they exist
            ITreeNode node = (ITreeNode) treeItem.getData ();
            List children = node.children();
            for (Iterator i = children.iterator(); i.hasNext();) {
                ITreeNode childNode = (ITreeNode) i.next();
                initTreeItem(new TreeItem(treeItem, SWT.NULL), childNode);
            }
        }
    };
    
    private FocusListener focusListener = new FocusAdapter() {
        public void focusGained(FocusEvent e) {
            ITreeNode node = (ITreeNode) e.widget.getData();
            fireFocusEvent(node);
        }
    };

	private void initTreeItem(final TreeItem item, ITreeNode node) {
		item.setText(node.getText());
		item.setData(node);
		if (node.mayHaveChildren()) {
            new TreeItem (item, 0); // Placeholder
		}
	}
    
    private LinkedList focusListeners = new LinkedList();
    
    /**
     * Adds the specified listener to the list of listeners that will
     * be notified whenever the browser pane's focus changes.
     * 
     * @param l The IFocusListener to add.
     */
    public void addFocusListener(IFocusListener l) {
        focusListeners.add(l);
    }
    
    /**
     * Removes the specified listener from the list of listeners that
     * will be notified whenever the browser pane's focus changes.
     * 
     * @param l The IFocusListener to remove.
     */
    public void removeFocusListener(IFocusListener l) {
        focusListeners.remove(l);
    }
    
    private void fireFocusEvent(ITreeNode node) {
        ObjectFocusEvent e = new ObjectFocusEvent(node);
        for (Iterator i = focusListeners.iterator(); i.hasNext();) {
            IFocusListener l = (IFocusListener) i.next();
            l.focusChanged(e);
        }
    }
    
}
