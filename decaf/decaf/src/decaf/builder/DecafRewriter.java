package decaf.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import sharpen.core.framework.*;

import decaf.config.*;
import decaf.core.*;

public class DecafRewriter {

	public static ASTRewrite rewrite(final ICompilationUnit element,
			IProgressMonitor monitor, TargetPlatform platform, DecafConfiguration decafConfig) {
		
		final CompilationUnit ast = parseCompilationUnit(element, monitor);
		ASTUtility.checkForProblems(ast, true);
		return rewrite(ast, platform, decafConfig);
	}

	private static ASTRewrite rewrite(final CompilationUnit unit, final TargetPlatform targetPlatform, DecafConfiguration decafConfig) {
		final ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
		final DecafRewritingContext context = new DecafRewritingContext(unit, rewrite, targetPlatform, decafConfig);
		context.run(new Runnable() {
			public void run() {
				
				if (targetPlatform != TargetPlatform.ANDROID) {
					unit.accept(new DecafRewritingVisitor(context));
				}
				
				unit.accept(new AnnotationRewritingVisitor(context));	
				unit.accept(new AccessibilityRewritingVisitor(context));
				
			}
		});
		return rewrite;
	}
	
	public static CompilationUnit parseCompilationUnit(ICompilationUnit unit, IProgressMonitor monitor) {
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit)parser.createAST(monitor);
	}

}
