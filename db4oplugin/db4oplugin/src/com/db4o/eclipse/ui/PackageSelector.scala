/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.db4o.eclipse.ui

import java.util._
import java.util.List

import org.eclipse.core.runtime._
import org.eclipse.jdt.core._
import org.eclipse.jdt.core.search._
import org.eclipse.jdt.core.search.SearchPattern
import org.eclipse.jdt.ui._
import org.eclipse.jface.dialogs._
import org.eclipse.jface.operation._
import org.eclipse.jface.viewers._
import org.eclipse.swt.graphics._
import org.eclipse.swt.widgets._
import org.eclipse.ui.dialogs._
import org.eclipse.jface.window._

object PackageSelector {
	val F_REMOVE_DUPLICATES= 1
	val F_SHOW_PARENTS= 2
	val F_HIDE_DEFAULT_PACKAGE= 4
	val F_HIDE_EMPTY_INNER= 8

	def createLabelProvider(dialogFlags: Int) = {
		var flags = JavaElementLabelProvider.SHOW_DEFAULT
		if ((dialogFlags & F_REMOVE_DUPLICATES) == 0) {
			flags= flags | JavaElementLabelProvider.SHOW_ROOT
		}
		new JavaElementLabelProvider(flags)
	}

}

class PackageSelector(parent: Shell, fContext: IRunnableContext, fFlags: Int, fScope: IJavaSearchScope, alreadySelected: Array[String]) 
		extends ElementListSelectionDialog(parent, PackageSelector.createLabelProvider(fFlags)) {

	/** The dialog location. */
	var fLocation: Point = null
	/** The dialog size. */
	var fSize: Point = null
	
	
	override def open(): Int = {
		var packageList= new ArrayList[IPackageFragment]()
		
		val runnable= new IRunnableWithProgress() {
			override def run(withMonitor: IProgressMonitor) {
				val monitor = withMonitor match {
				  case null => new NullProgressMonitor()
				  case _ => withMonitor 
				}
				val hideEmpty= (fFlags & PackageSelector.F_HIDE_EMPTY_INNER) != 0
				monitor.beginTask("Collecting Packages", if(hideEmpty) 2 else 1)
				try {
					val requestor= new SearchRequestor() {
						val fSet= new HashSet[String]();
						val fAddDefault= (fFlags & PackageSelector.F_HIDE_DEFAULT_PACKAGE) == 0
						val fDuplicates= (fFlags & PackageSelector.F_REMOVE_DUPLICATES) == 0
						val fIncludeParents= (fFlags & PackageSelector.F_SHOW_PARENTS) != 0

						override def acceptSearchMatch(searchMatch: SearchMatch) {
							val enclosingElement = searchMatch.getElement().asInstanceOf[IJavaElement]
							val name = enclosingElement.getElementName
							if (!fAddDefault && name.length() == 0) {
								return
							}
							if (!fDuplicates && !fSet.add(name)) {
								return
							}
							val packageRoot = packageFragmentRootFor(enclosingElement)
							if(packageRoot.getKind() != IPackageFragmentRoot.K_SOURCE) {
								return;
							}

							val packageFragment = enclosingElement.asInstanceOf[IPackageFragment]
							addPackageFragment(packageFragment)
							if (fIncludeParents) {
								addParentPackages(enclosingElement, name)
							}
						}

						private def packageFragmentRootFor(javaElement: IJavaElement): IPackageFragmentRoot = {
							if(javaElement.isInstanceOf[IPackageFragmentRoot]) {
								return javaElement.asInstanceOf[IPackageFragmentRoot]
							}
							if(javaElement == null) {
								return null
							}
							packageFragmentRootFor(javaElement.getParent)
						}
						
						private def addParentPackages(enclosingElement: IJavaElement, name: String) {
							var nameFragment = name
							val root = enclosingElement.getParent.asInstanceOf[IPackageFragmentRoot]
							var idx= nameFragment.lastIndexOf('.')
							while (idx != -1) {
								nameFragment = nameFragment.substring(0, idx)
								if (fDuplicates || fSet.add(nameFragment)) {
									addPackageFragment(root.getPackageFragment(nameFragment))
								}
								idx = nameFragment.lastIndexOf('.')
							}
						}
					}
					val pattern= SearchPattern.createPattern("*", //$NON-NLS-1$
							IJavaSearchConstants.PACKAGE, IJavaSearchConstants.DECLARATIONS,
							SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE)
					new SearchEngine().search(pattern, Array(SearchEngine.getDefaultSearchParticipant), fScope, requestor, new SubProgressMonitor(monitor, 1))
					
					if (monitor.isCanceled()) {
						throw new InterruptedException()
					}

					if (hideEmpty) {
						removeEmptyPackages(new SubProgressMonitor(monitor, 1))
					}
				} catch {
				  	case e: CoreException => throw new java.lang.reflect.InvocationTargetException(e)
				  	case e: OperationCanceledException => throw new InterruptedException()
				} 
				finally {
					monitor.done()
				}
			}
			
			private def removeEmptyPackages(monitor: IProgressMonitor) {
				monitor.beginTask("Filtering Empty Packages", packageList.size)
				try {
					val res = new ArrayList[IPackageFragment](packageList.size)
					for (i <- 0 until packageList.size) {
						val pkg = packageList.get(i).asInstanceOf[IPackageFragment]
						if (pkg.hasChildren || !pkg.hasSubpackages) {
							res.add(pkg)
						}
						monitor.worked(1)
						if (monitor.isCanceled()) {
							throw new InterruptedException()
						}
					}
					packageList.clear()
					packageList.addAll(res)
				} 
				finally {
					monitor.done()
				}
			}
			
			private def addPackageFragment(fragment: IPackageFragment) {
				for (packageName <- alreadySelected) {
					if(packageName.equals(fragment.getElementName)) {
						return
					}
				}
				packageList.add(fragment)
			}
		}

		try {
			fContext.run(true, true, runnable)
		} 
		catch {
		  case e: java.lang.reflect.InvocationTargetException => {
			// FIXME
			//ExceptionHandler.handle(e, JavaUIMessages.PackageSelectionDialog_error_title, JavaUIMessages.PackageSelectionDialog_error3Message); 
			e.printStackTrace
			return Window.CANCEL
		  } 
		  case e: InterruptedException => {
			// cancelled by user
			e.printStackTrace
			return Window.CANCEL
		  }
		}
		
		if (packageList.isEmpty) {
			val title= "No packages found"
			val message= "There are no packages for this project"
			MessageDialog.openInformation(getShell, title, message)
			return Window.CANCEL
		}
		
		setElements(packageList.toArray)

		super.open
	}
	
	
	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	override def configureShell(newShell: Shell) {
		super.configureShell(newShell)
		// FIXME
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.OPEN_PACKAGE_DIALOG);
	}

	/*
	 * @see Window#close()
	 */
	override def close() = {
		writeSettings
		super.close
	}

	/*
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	override def createContents(parent: Composite) = {
		val control = super.createContents(parent)
		readSettings
		control
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	override def getInitialSize = {
		val result = super.getInitialSize()
		if (fSize != null) {
			result.x= Math.max(result.x, fSize.x)
			result.y= Math.max(result.y, fSize.y)
			val display= getShell.getDisplay.getClientArea
			result.x= Math.min(result.x, display.width)
			result.y= Math.min(result.y, display.height)
		}
		result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	override def getInitialLocation(initialSize: Point) = {
		val result = super.getInitialLocation(initialSize)
		if (fLocation != null) {
			result.x = fLocation.x
			result.y = fLocation.y
			val display = getShell.getDisplay.getClientArea
			val xe = result.x + initialSize.x
			if (xe > display.width) {
				result.x -= xe - display.width 
			}
			val ye = result.y + initialSize.y
			if (ye > display.height) {
				result.y -= ye - display.height
			}
		}
		result
	}



	/**
	 * Initializes itself from the dialog settings with the same state
	 * as at the previous invocation.
	 */
	def readSettings() {
		val s = getDialogSettings
		try {
			val x = s.getInt("x") //$NON-NLS-1$
			val y = s.getInt("y") //$NON-NLS-1$
			fLocation = new Point(x, y)
			val width = s.getInt("width") //$NON-NLS-1$
			val height = s.getInt("height") //$NON-NLS-1$
			fSize = new Point(width, height)
		} 
		catch {
		  case e: NumberFormatException => {
			fLocation = null
			fSize = null
		  }
		}
	}

	/**
	 * Stores it current configuration in the dialog store.
	 */
	def writeSettings {
		val s = getDialogSettings

		val location = getShell.getLocation
		s.put("x", location.x) //$NON-NLS-1$
		s.put("y", location.y) //$NON-NLS-1$

		val size = getShell.getSize;
		s.put("width", size.x) //$NON-NLS-1$
		s.put("height", size.y) //$NON-NLS-1$
	}

	/**
	 * Returns the dialog settings object used to share state
	 * between several find/replace dialogs.
	 *
	 * @return the dialog settings to be used
	 */
	def getDialogSettings() = {
		new DialogSettings("Workbench")
		// FIXME: need to port to Scala to be able to access Db4oPluginActivator
//		IDialogSettings settings= Db4oPluginActivator.getDefault().getDialogSettings();
//		String sectionName= getClass().getName();
//		IDialogSettings subSettings= settings.getSection(sectionName);
//		if (subSettings == null)
//			subSettings= settings.addNewSection(sectionName);
//		return subSettings;
	}



}
