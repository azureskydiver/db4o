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
		final AST ast = unit.getAST();
		final ASTRewrite rewrite = ASTRewrite.create(ast);
		unit.accept(new DecafRewritingVisitor(ast, rewrite, decafConfig));
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
