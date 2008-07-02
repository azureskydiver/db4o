package decaf.tests;

import sharpen.ui.tests.*;

public class DecafTestResource extends TestCaseResource {

	private static final String PATH_SUFFIX = ".decaf.txt";
	private final TargetPlatform _targetPlatform;
	
	public DecafTestResource(String originalPath) {
		this(originalPath, null);
	}

	public DecafTestResource(String originalPath, TargetPlatform targetPlatform) {
		super(originalPath);
		_targetPlatform = targetPlatform;
	}

	@Override
	public String packageName() {
		return !_targetPlatform.hasFileIDPart() ? super.packageName() : super.packageName() + "." + _targetPlatform.fileIDPart();
	}
	
	@Override
	protected String expectedPathSuffix() {
		if(!_targetPlatform.hasFileIDPart()) {
			return PATH_SUFFIX;
		}
		return "." + _targetPlatform.fileIDPart() + PATH_SUFFIX;
	}

}
