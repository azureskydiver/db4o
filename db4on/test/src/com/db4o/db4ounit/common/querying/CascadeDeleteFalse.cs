namespace com.db4o.db4ounit.common.querying
{
	public class CascadeDeleteFalse : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class CascadeDeleteFalseHelper
		{
		}

		public com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
			 h1;

		public com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
			 h2;

		public com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
			 h3;

		protected override void Configure(com.db4o.config.Configuration conf)
		{
			conf.ObjectClass(this).CascadeOnDelete(true);
			conf.ObjectClass(this).ObjectField("h3").CascadeOnDelete(false);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadeDeleteFalse cdf = new com.db4o.db4ounit.common.querying.CascadeDeleteFalse
				();
			cdf.h1 = new com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
				();
			cdf.h2 = new com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
				();
			cdf.h3 = new com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper
				();
			Db().Set(cdf);
		}

		public virtual void Test()
		{
			CheckHelperCount(3);
			com.db4o.db4ounit.common.querying.CascadeDeleteFalse cdf = (com.db4o.db4ounit.common.querying.CascadeDeleteFalse
				)RetrieveOnlyInstance(GetType());
			Db().Delete(cdf);
			CheckHelperCount(1);
		}

		private void CheckHelperCount(int count)
		{
			Db4oUnit.Assert.AreEqual(count, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteFalse.CascadeDeleteFalseHelper)
				));
		}
	}
}
