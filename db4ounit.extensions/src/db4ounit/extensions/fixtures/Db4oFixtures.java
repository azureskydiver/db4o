package db4ounit.extensions.fixtures;

public class Db4oFixtures {

	public static ConfigurationSource configSource(boolean independentConfig) {
    	ConfigurationSource configSource = new IndependentConfigurationSource();
    	if(!independentConfig) {
    		configSource = new CachingConfigurationSource(configSource);
    	}
    	return configSource;
    }

	public static Db4oClientServer newEmbeddedCS(boolean independentConfig) {
        return new Db4oClientServer(configSource(independentConfig), true, "C/S EMBEDDED");
    }

	public static Db4oClientServer newNetworkingCS(boolean independentConfig) {
        return new Db4oClientServer(configSource(independentConfig), false, "C/S");
    }

	public static Db4oSolo newSolo(boolean independentConfig) {
        return new Db4oSolo(configSource(independentConfig));
    }

}
