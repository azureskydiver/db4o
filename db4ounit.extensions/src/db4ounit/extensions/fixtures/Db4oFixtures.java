package db4ounit.extensions.fixtures;

public class Db4oFixtures {

	public static Db4oClientServer newEmbeddedCS() {
        return new Db4oClientServer(true, "C/S EMBEDDED");
    }

	public static Db4oClientServer newNetworkingCS() {
        return new Db4oClientServer(false, "C/S");
    }

	public static Db4oSolo newSolo() {
        return new Db4oSolo();
    }

}
