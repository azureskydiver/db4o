package decaf.builder;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public interface ASTProvider {
	
	CompilationUnit forCompilationUnit(ICompilationUnit unit, IProgressMonitor monitor);

}
