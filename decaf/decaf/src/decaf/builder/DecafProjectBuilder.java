package decaf.builder;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jdt.internal.core.util.*;
import org.eclipse.jface.text.*;
import org.eclipse.ui.*;

public class DecafProjectBuilder extends IncrementalProjectBuilder {

	public static IJavaProject decafProjectFor(IJavaProject javaProject) throws CoreException {
		IWorkspaceRoot root = javaProject.getProject().getWorkspace().getRoot();
		String decafProjectName = javaProject.getElementName() + ".decaf";
		IProject decafProject = root.getProject(decafProjectName);
		WorkspaceUtilities.initializeProject(decafProject, null);
		WorkspaceUtilities.addProjectNature(decafProject, JavaCore.NATURE_ID);
		
		IJavaProject decafJavaProject = JavaCore.create(decafProject);
		decafJavaProject.setRawClasspath(mapClasspathEntries(javaProject, decafJavaProject), null);
		return decafJavaProject;
	}

	public static final String BUILDER_ID = "decaf.decafBuilder";

	private static final String MARKER_TYPE = "decaf.decafProblem";

	protected void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void decaf(IResource resource, IProgressMonitor monitor) throws CoreException {
		if (isJavaFile(resource)) {
			monitor.subTask(resource.getName());
			final IFile file = (IFile) resource;
//			deleteMarkers(file);
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					decafFile(monitor, file);
				}
				
			}, monitor);
			
		}
	}

	private boolean isJavaFile(IResource resource) {
		return resource instanceof IFile && resource.getName().endsWith(".java");
	}

	private void decafFile(final IProgressMonitor monitor, IFile file)
			throws CoreException, JavaModelException {
		final ICompilationUnit element = compilationUnitFor(file);
		final ICompilationUnit decaf = decafElementFor(element);
		final ASTRewrite rewrite = DecafRewriter.rewrite(element, monitor);
		
		if (!PlatformUI.isWorkbenchRunning()) {
			rewriteFile(decaf, rewrite, monitor);
			return;
		}
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				rewriteFile(decaf, rewrite, monitor);
			}
		});
	}

	private ICompilationUnit decafElementFor(ICompilationUnit element) throws CoreException {
		IJavaProject srcJavaProject = element.getJavaProject();
		
		IJavaProject decafJavaProject = decafProjectFor(srcJavaProject);
		IFile resource = fileFor(element);
		
		IFile decafFile = decafJavaProject.getProject().getFile(resource.getProjectRelativePath());
		if (!decafFile.exists()) {
			copy(resource, decafFile);
			return compilationUnitFor(decafFile);
		}
		
		ICompilationUnit decafElement = compilationUnitFor(decafFile);
		updateDecafUnit(element, decafElement);
		return decafElement;
	}

	private void copy(IFile fromFile, IFile toFile) throws CoreException {
		WorkspaceUtilities.initializeTree((IFolder)toFile.getParent(), null);
		fromFile.copy(toFile.getFullPath(), true, null);
	}

	private ICompilationUnit compilationUnitFor(IFile decafFile) {
		return (ICompilationUnit)JavaCore.create(decafFile);
	}

	private void updateDecafUnit(ICompilationUnit element,
			ICompilationUnit decafElement) throws JavaModelException,
			CoreException {
		if (decafElement.isOpen()) {
			decafElement.getBuffer().setContents(element.getBuffer().getCharacters());
		} else {
			fileFor(decafElement).setContents(fileFor(element).getContents(), true, false, null);
		}
	}

	private static IFile fileFor(ICompilationUnit decafElement) {
		return (IFile)decafElement.getResource();
	}

	private static IClasspathEntry[] mapClasspathEntries(IJavaProject javaProject,
			IJavaProject decafJavaProject) throws JavaModelException,
			CoreException {
		IClasspathEntry[] srcClasspath = javaProject.getRawClasspath();
		IClasspathEntry[] targetClasspath = new IClasspathEntry[srcClasspath.length];
		for (int i=0; i<srcClasspath.length; ++i) {
			IClasspathEntry entry = srcClasspath[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				targetClasspath[i] = createSourceFolder(decafJavaProject, entry.getPath());
			} else {
				targetClasspath[i] = entry;
			}
		}
		return targetClasspath;
	}

	private static IClasspathEntry createSourceFolder(IJavaProject decafJavaProject,
			IPath path) throws CoreException {
		
		IFolder folder = decafJavaProject.getProject().getFolder(path.removeFirstSegments(1));
		WorkspaceUtilities.initializeTree(folder, null);
		return JavaCore.newSourceEntry(folder.getFullPath(), new IPath[] {});
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		getProject().accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				decaf(resource, monitor);
				return true;
			}
		});
	}
	
	protected void incrementalBuild(IResourceDelta delta,
			final IProgressMonitor monitor) throws CoreException {
		delta.accept(new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					decaf(resource, monitor);
					break;
				case IResourceDelta.REMOVED:
					break;
				case IResourceDelta.CHANGED:
					decaf(resource, monitor);
					break;
				}
				return true;
			}	
		});
	}
	
	@SuppressWarnings("restriction")
	private void rewriteDocument(final ASTRewrite rewrite,
			final ICompilationUnit decaf) throws JavaModelException,
			BadLocationException {
		SimpleDocument doc = new SimpleDocument(decaf.getBuffer().getContents());
		rewrite.rewriteAST().apply(doc);
		decaf.getBuffer().setContents(doc.get());
	}

	private void rewriteFile(final ICompilationUnit decaf, final ASTRewrite rewrite,
			IProgressMonitor monitor) {
		IPath path= decaf.getPath();
		try {
//			if (null != decaf.getBuffer() /*&& decaf.isWorkingCopy()*/) {
//				rewriteDocument(rewrite, decaf);
//			} else {
				FileRewriter.rewriteFile(rewrite, path);
//			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
