package decaf.builder;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import decaf.core.*;

public class DecafRewritingContext {
	
	private final ASTRewrite _rewrite;
	private final DecafASTNodeBuilder _builder;
	private final TargetPlatform _targetPlatform;
	
	public DecafRewritingContext(CompilationUnit unit, ASTRewrite rewrite, TargetPlatform targetPlatform, DecafConfiguration decafConfig) {
		_builder = new DecafASTNodeBuilder(unit, decafConfig);
		_rewrite = rewrite;
		_targetPlatform = targetPlatform;
	}
	
	public TargetPlatform targetPlatform() {
		return _targetPlatform;
	}

	public DecafASTNodeBuilder builder() {
		return _builder;
	}

	public ASTRewrite rewrite() {
		return _rewrite;
	}

}
