package decaf.builder;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jface.text.*;
import org.eclipse.ui.*;

import sharpen.core.framework.resources.*;
import decaf.core.*;

public class DecafProjectBuilder extends IncrementalProjectBuilder {

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
		
		final DecafProject properties = DecafProject.create(element.getJavaProject());
		
		for (DecafProject.OutputTarget outputTarget : properties.targets()) {
			
			final ICompilationUnit decaf = decafElementFor(element, outputTarget.targetProject());
			final ASTRewrite rewrite = DecafRewriter.rewrite(element, monitor, outputTarget.targetPlatform().config());
			
			safeRewriteFile(decaf, rewrite, monitor);
		}
	}

	private void safeRewriteFile(final ICompilationUnit decaf, final ASTRewrite rewrite, final IProgressMonitor monitor) {
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

	private ICompilationUnit decafElementFor(
			ICompilationUnit sourceCompilationUnit,
			IJavaProject decafJavaProject) throws CoreException,
			JavaModelException {
		IFile resource = fileFor(sourceCompilationUnit);
		
		IFile decafFile = decafJavaProject.getProject().getFile(resource.getProjectRelativePath());
		if (!decafFile.exists()) {
			copy(resource, decafFile);
			return compilationUnitFor(decafFile);
		}
		
		ICompilationUnit decafElement = compilationUnitFor(decafFile);
		updateDecafUnit(sourceCompilationUnit, decafElement);
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
	
	private void rewriteFile(final ICompilationUnit decaf, final ASTRewrite rewrite,
			IProgressMonitor monitor) {
		IPath path= decaf.getPath();
		try {
			FileRewriter.rewriteFile(rewrite, path);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
}
