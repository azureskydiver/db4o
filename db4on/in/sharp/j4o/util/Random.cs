namespace j4o.util {
	public class Random {
		public Random() {
		}
		
		public long nextLong() {
			return j4o.lang.JavaSystem.currentTimeMillis();
		}
	}
}