package decaf.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class DecafRewriter {

	private static final class ASTProviderImpl implements ASTProvider {
		final ASTParser parser = ASTParser.newParser(AST.JLS3);

		public CompilationUnit forCompilationUnit(ICompilationUnit unit, IProgressMonitor monitor) {
			parser.setSource(unit);
			parser.setResolveBindings(true);
			return (CompilationUnit)parser.createAST(monitor);
		}
	}

	public static ASTRewrite rewrite(final ICompilationUnit element,
			IProgressMonitor monitor, DecafConfiguration decafConfig) {
		
		final ASTProvider provider = new ASTProviderImpl();
		final CompilationUnit unit = provider.forCompilationUnit(element, monitor);
		final ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
		unit.accept(new DecafRewritingVisitor(provider, unit, rewrite, decafConfig));
		return rewrite;
	}
}
