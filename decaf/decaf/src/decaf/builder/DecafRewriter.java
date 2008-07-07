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
		final CompilationUnit unit = parseCompilationUnit(monitor, element);
		final ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
		unit.accept(new DecafRewritingVisitor(unit, rewrite, decafConfig));
		return rewrite;
	}

	private static CompilationUnit parseCompilationUnit(IProgressMonitor monitor,
			ICompilationUnit element) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(element);
		parser.setResolveBindings(true);
		return (CompilationUnit)parser.createAST(monitor);
	}

}
