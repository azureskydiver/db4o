package decaf.builder;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import decaf.config.*;
import decaf.core.*;
import decaf.rewrite.*;

public class DecafRewritingContext {
	
	private final DecafRewritingServices _rewrite;
	private final DecafASTNodeBuilder _builder;
	private final TargetPlatform _targetPlatform;
	
	public DecafRewritingContext(CompilationUnit unit, ASTRewrite rewrite, TargetPlatform targetPlatform, DecafConfiguration decafConfig) {
		_builder = new DecafASTNodeBuilder(unit, decafConfig);
		_rewrite = new DecafRewritingServices(rewrite, _builder);
		_targetPlatform = targetPlatform;
	}
	
	public TargetPlatform targetPlatform() {
		return _targetPlatform;
	}

	public DecafASTNodeBuilder builder() {
		return _builder;
	}

	public DecafRewritingServices rewrite() {
		return _rewrite;
	}
}
