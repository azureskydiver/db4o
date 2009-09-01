package db4ounit.extensions.fixtures;

public class Db4oFixtures {

	public static Db4oSolo newSolo() {
		return new Db4oSolo();
	}
	
	public static Db4oInMemory newInMemory() {
		return new Db4oInMemory();
	}
	
	public static MultiSessionFixture newEmbedded() {
        return new Db4oEmbeddedSessionFixture();
    }

	public static MultiSessionFixture newEmbedded(String label) {
		return new Db4oEmbeddedSessionFixture(label);
	}
	
	public static Db4oNetworking newNetworkingCS() {
        return new Db4oNetworking();
    }
	
	public static Db4oNetworking newNetworkingCS(String label) {
		return new Db4oNetworking(label);
	}


}
