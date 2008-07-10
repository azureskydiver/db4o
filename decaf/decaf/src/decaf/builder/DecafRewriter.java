package decaf.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class DecafRewriter {

	public static ASTRewrite rewrite(final ICompilationUnit element,
			IProgressMonitor monitor, DecafConfiguration decafConfig) {
		
		final CompilationUnit unit = parseCompilationUnit(element, monitor);
		final ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
		unit.accept(new DecafRewritingVisitor(unit, rewrite, decafConfig));
		return rewrite;
	}
	
	private static CompilationUnit parseCompilationUnit(ICompilationUnit unit, IProgressMonitor monitor) {
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit)parser.createAST(monitor);
	}

}
