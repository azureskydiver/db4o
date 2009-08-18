/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.IO;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Tests.Common.Exceptions;
using Sharpen;

namespace Db4objects.Db4o.Tests.Common.Exceptions
{
	public class InvalidSlotExceptionTestCase : AbstractDb4oTestCase
	{
		private const int InvalidId = 3;

		private const int OutOfMemoryId = 4;

		public static void Main(string[] args)
		{
			new InvalidSlotExceptionTestCase().RunAll();
		}

		/// <exception cref="System.Exception"></exception>
		protected override void Configure(IConfiguration config)
		{
			config.Storage = new InvalidSlotExceptionTestCase.MockStorage();
		}

		/// <exception cref="System.Exception"></exception>
		public virtual void TestInvalidSlotException()
		{
			Assert.Expect(typeof(Db4oRecoverableException), new _ICodeBlock_29(this));
			Assert.IsFalse(Db().IsClosed());
		}

		private sealed class _ICodeBlock_29 : ICodeBlock
		{
			public _ICodeBlock_29(InvalidSlotExceptionTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			/// <exception cref="System.Exception"></exception>
			public void Run()
			{
				this._enclosing.Db().GetByID(InvalidSlotExceptionTestCase.InvalidId);
			}

			private readonly InvalidSlotExceptionTestCase _enclosing;
		}

		public virtual void TestDbNotClosedOnOutOfMemory()
		{
			Assert.Expect(typeof(Db4oRecoverableException), typeof(OutOfMemoryException), new 
				_ICodeBlock_38(this));
			Assert.IsFalse(Db().IsClosed());
		}

		private sealed class _ICodeBlock_38 : ICodeBlock
		{
			public _ICodeBlock_38(InvalidSlotExceptionTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			/// <exception cref="System.Exception"></exception>
			public void Run()
			{
				this._enclosing.Db().GetByID(InvalidSlotExceptionTestCase.OutOfMemoryId);
			}

			private readonly InvalidSlotExceptionTestCase _enclosing;
		}

		public class A
		{
			internal InvalidSlotExceptionTestCase.A _a;

			public A(InvalidSlotExceptionTestCase.A a)
			{
				this._a = a;
			}
		}

		public class MockStorage : StorageDecorator
		{
			public MockStorage() : base(new FileStorage())
			{
			}

			protected override IBin Decorate(BinConfiguration config, IBin bin)
			{
				return new InvalidSlotExceptionTestCase.MockStorage.MockBin(bin);
			}

			private class MockBin : BinDecorator
			{
				private bool _deliverInvalidSlot;

				public MockBin(IBin bin) : base(bin)
				{
				}

				/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
				public override int Read(long pos, byte[] bytes, int length)
				{
					Seek(pos);
					if (_deliverInvalidSlot)
					{
						ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.PointerLength);
						buffer.WriteInt(1);
						buffer.WriteInt(int.MaxValue);
						System.Array.Copy(buffer._buffer, 0, bytes, 0, Const4.PointerLength);
						return length;
					}
					return base.Read(pos, bytes, length);
				}

				/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
				private void Seek(long pos)
				{
					if (pos == OutOfMemoryId)
					{
						throw new OutOfMemoryException();
					}
					if (pos == InvalidId)
					{
						_deliverInvalidSlot = true;
						return;
					}
					_deliverInvalidSlot = false;
				}
			}
		}
	}
}
