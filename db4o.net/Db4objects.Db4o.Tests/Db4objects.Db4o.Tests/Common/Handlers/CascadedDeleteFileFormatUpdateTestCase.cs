/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Tests.Common.Handlers;

namespace Db4objects.Db4o.Tests.Common.Handlers
{
	/// <exclude></exclude>
	public class CascadedDeleteFileFormatUpdateTestCase : FormatMigrationTestCaseBase
	{
		private bool _failed;

		protected override void Configure(IConfiguration config)
		{
			config.ObjectClass(typeof(CascadedDeleteFileFormatUpdateTestCase.ParentItem)).CascadeOnDelete
				(true);
			config.Diagnostic().AddListener(new _IDiagnosticListener_23(this));
		}

		private sealed class _IDiagnosticListener_23 : IDiagnosticListener
		{
			public _IDiagnosticListener_23(CascadedDeleteFileFormatUpdateTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void OnDiagnostic(IDiagnostic d)
			{
				if (d is DeletionFailed)
				{
					this._enclosing._failed = true;
				}
			}

			private readonly CascadedDeleteFileFormatUpdateTestCase _enclosing;
		}

		public class ParentItem
		{
			public CascadedDeleteFileFormatUpdateTestCase.ChildItem[] _children;

			public static CascadedDeleteFileFormatUpdateTestCase.ParentItem NewTestInstance()
			{
				CascadedDeleteFileFormatUpdateTestCase.ParentItem item = new CascadedDeleteFileFormatUpdateTestCase.ParentItem
					();
				item._children = new CascadedDeleteFileFormatUpdateTestCase.ChildItem[] { new CascadedDeleteFileFormatUpdateTestCase.ChildItem
					(), new CascadedDeleteFileFormatUpdateTestCase.ChildItem() };
				return item;
			}
		}

		public class ChildItem
		{
		}

		protected override void AssertObjectsAreReadable(IExtObjectContainer objectContainer
			)
		{
			CascadedDeleteFileFormatUpdateTestCase.ParentItem parentItem = (CascadedDeleteFileFormatUpdateTestCase.ParentItem
				)RetrieveInstance(objectContainer, typeof(CascadedDeleteFileFormatUpdateTestCase.ParentItem
				));
			Assert.IsNotNull(parentItem._children);
			Assert.IsNotNull(parentItem._children[0]);
			Assert.IsNotNull(parentItem._children[1]);
			objectContainer.Delete(parentItem);
			Assert.IsFalse(_failed);
			Store(objectContainer);
		}

		private object RetrieveInstance(IExtObjectContainer objectContainer, Type clazz)
		{
			return objectContainer.Query(clazz).Next();
		}

		protected override string FileNamePrefix()
		{
			return "migrate_cascadedelete_";
		}

		protected override void Store(IExtObjectContainer objectContainer)
		{
			objectContainer.Set(CascadedDeleteFileFormatUpdateTestCase.ParentItem.NewTestInstance
				());
		}

		protected override string[] VersionNames()
		{
			return new string[] { Sharpen.Runtime.Substring(Db4oFactory.Version(), 5) };
		}
	}
}
