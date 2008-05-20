package db4ounit.extensions.fixtures;

import com.db4o.config.*;

public class CachingConfigurationSource implements ConfigurationSource {

	private final ConfigurationSource _source;
	private Configuration _cached;
	
	public CachingConfigurationSource(ConfigurationSource source) {
		_source = source;
	}
	
	public Configuration config() {
		if(_cached == null) {
			_cached = _source.config();
		}
		return _cached;
	}

	public void reset() {
		_cached = null;
	}
}
