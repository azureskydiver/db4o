package com.db4o.browser.eclipse.views;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.db4o.browser.gui.standalone.Model;
import com.db4o.browser.gui.views.DbBrowserPane;


public class DbBrowser extends ViewPart implements ISelectionProvider, ISelectionListener {

    /**
	 * @see org.eclipse.ui.part.ViewPart#init(IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
        site.setSelectionProvider(this);

        site.getPage().addSelectionListener(this);
    }
	
    // Be able to be an IPropertySheetPage-------------------------------
    
    /**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return null;
        }
        else return super.getAdapter(adapter);
	}
    
    // Be an ISelectionProvider-----------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {

    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {

    }

    // Be an ISelectionListener ----------------------------------------------------------
    
    private String currentFile = "";

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection _selection = (IStructuredSelection) selection;
            if (_selection.size() == 1) {
                Object selected = _selection.getFirstElement();
                if (selected instanceof IFile) {
                    IFile file = (IFile) selected;
                    if (file.getFileExtension().equals("yap")) {
                        String selectedFileName = file.getRawLocation().toString();
                        if (!currentFile.equals(selectedFileName)) {
                            if (model != null) model.close();
                            model = new Model(selectedFileName);
                        	ui.setInput(model);
                            currentFile = selectedFileName;
                        }
                    }
                }
            }
        }
	}
    
    private Model model = null;
    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        if (model != null) {
            model.close();
        }
    }
    

    // Now the normal Viewer stuff... ------------------------------------
    
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
        ui = new DbBrowserPane(parent, SWT.NULL);
    }
    
    private DbBrowserPane ui = null;

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
        ui.setFocus();
	}
}