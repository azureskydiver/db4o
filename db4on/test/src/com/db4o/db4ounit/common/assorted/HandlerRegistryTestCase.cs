namespace com.db4o.db4ounit.common.assorted
{
	public class HandlerRegistryTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public interface FooInterface
		{
		}

		public virtual void _testInterfaceHandlerIsSameAsObjectHandler()
		{
			Db4oUnit.Assert.AreSame(HandlerForClass(typeof(object)), HandlerForClass(typeof(com.db4o.db4ounit.common.assorted.HandlerRegistryTestCase.FooInterface)
				));
		}

		private com.db4o.@internal.TypeHandler4 HandlerForClass(System.Type clazz)
		{
			return Handlers().HandlerForClass(Stream(), ReflectClass(clazz));
		}

		private com.db4o.@internal.HandlerRegistry Handlers()
		{
			return Stream().Handlers();
		}
	}
}
