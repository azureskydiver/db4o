namespace com.db4o.reflect.self
{
	public interface SelfReflectable
	{
		object self_get(string fieldName);

		void self_set(string fieldName, object value);
	}
}
