namespace com.db4o.defragment
{
	/// <summary>defragments database files.</summary>
	/// <remarks>
	/// defragments database files.
	/// <br /><br />db4o structures storage inside database files as free and occupied slots, very
	/// much like a file system - and just like a file system it can be fragmented.<br /><br />
	/// The simplest way to defragment a database file:<br /><br />
	/// <code>Defragment.defrag("sample.yap");</code><br /><br />
	/// This will move the file to "sample.yap.backup", then create a defragmented
	/// version of this file in the original position, using a temporary file
	/// "sample.yap.mapping". If the backup file already exists, this will throw an
	/// exception and no action will be taken.<br /><br />
	/// For more detailed configuration of the defragmentation process, provide a
	/// DefragmentConfig instance:<br /><br />
	/// <code>DefragmentConfig config=new DefragmentConfig("sample.yap","sample.bap",new BTreeIDMapping("sample.map"));<br />
	/// config.forceBackupDelete(true);<br />
	/// config.storedClassFilter(new AvailableClassFilter());<br />
	/// config.db4oConfig(db4oConfig);<br />
	/// Defragment.defrag(config);</code><br /><br />
	/// This will move the file to "sample.bap", then create a defragmented version
	/// of this file in the original position, using a temporary file "sample.map" for BTree mapping.
	/// If the backup file already exists, it will be deleted. The defragmentation
	/// process will skip all classes that have instances stored within the yap file,
	/// but that are not available on the class path (through the current
	/// classloader). Custom db4o configuration options are read from the
	/// <see cref="com.db4o.config.Configuration">Configuration</see>
	/// passed as db4oConfig.
	/// <strong>Note:</strong> For some specific, non-default configuration settings like
	/// UUID generation, etc., you <strong>must</strong> pass an appropriate db4o configuration,
	/// just like you'd use it within your application for normal database operation.
	/// </remarks>
	public class Defragment
	{
		/// <summary>
		/// Renames the file at the given original path to a backup file and then
		/// builds a defragmented version of the file in the original place.
		/// </summary>
		/// <remarks>
		/// Renames the file at the given original path to a backup file and then
		/// builds a defragmented version of the file in the original place.
		/// </remarks>
		/// <param name="origPath">The path to the file to be defragmented.</param>
		/// <exception cref="System.IO.IOException">if the original file cannot be moved to the backup location
		/// 	</exception>
		public static void Defrag(string origPath)
		{
			Defrag(new com.db4o.defragment.DefragmentConfig(origPath), new com.db4o.defragment.Defragment.NullListener
				());
		}

		/// <summary>
		/// Renames the file at the given original path to the given backup file and
		/// then builds a defragmented version of the file in the original place.
		/// </summary>
		/// <remarks>
		/// Renames the file at the given original path to the given backup file and
		/// then builds a defragmented version of the file in the original place.
		/// </remarks>
		/// <param name="origPath">The path to the file to be defragmented.</param>
		/// <param name="backupPath">The path to the backup file to be created.</param>
		/// <exception cref="System.IO.IOException">if the original file cannot be moved to the backup location
		/// 	</exception>
		public static void Defrag(string origPath, string backupPath)
		{
			Defrag(new com.db4o.defragment.DefragmentConfig(origPath, backupPath), new com.db4o.defragment.Defragment.NullListener
				());
		}

		/// <summary>
		/// Renames the file at the configured original path to the configured backup
		/// path and then builds a defragmented version of the file in the original
		/// place.
		/// </summary>
		/// <remarks>
		/// Renames the file at the configured original path to the configured backup
		/// path and then builds a defragmented version of the file in the original
		/// place.
		/// </remarks>
		/// <param name="config">The configuration for this defragmentation run.</param>
		/// <exception cref="System.IO.IOException">if the original file cannot be moved to the backup location
		/// 	</exception>
		public static void Defrag(com.db4o.defragment.DefragmentConfig config)
		{
			Defrag(config, new com.db4o.defragment.Defragment.NullListener());
		}

		/// <summary>
		/// Renames the file at the configured original path to the configured backup
		/// path and then builds a defragmented version of the file in the original
		/// place.
		/// </summary>
		/// <remarks>
		/// Renames the file at the configured original path to the configured backup
		/// path and then builds a defragmented version of the file in the original
		/// place.
		/// </remarks>
		/// <param name="config">The configuration for this defragmentation run.</param>
		/// <param name="listener">
		/// A listener for status notifications during the defragmentation
		/// process.
		/// </param>
		/// <exception cref="System.IO.IOException">if the original file cannot be moved to the backup location
		/// 	</exception>
		public static void Defrag(com.db4o.defragment.DefragmentConfig config, com.db4o.defragment.DefragmentListener
			 listener)
		{
			j4o.io.File backupFile = new j4o.io.File(config.BackupPath());
			if (backupFile.Exists())
			{
				if (!config.ForceBackupDelete())
				{
					throw new System.IO.IOException("Could not use '" + config.BackupPath() + "' as backup path - file exists."
						);
				}
				backupFile.Delete();
			}
			System.IO.File.Move(config.OrigPath(), config.BackupPath());
			com.db4o.defragment.DefragContextImpl context = new com.db4o.defragment.DefragContextImpl
				(config, listener);
			int newClassCollectionID = 0;
			int targetIdentityID = 0;
			int targetUuidIndexID = 0;
			try
			{
				FirstPass(context, config);
				SecondPass(context, config);
				DefragUnindexed(context);
				newClassCollectionID = context.MappedID(context.SourceClassCollectionID());
				context.TargetClassCollectionID(newClassCollectionID);
				int sourceIdentityID = context.DatabaseIdentityID(com.db4o.defragment.DefragContextImpl
					.SOURCEDB);
				targetIdentityID = context.MappedID(sourceIdentityID, 0);
				targetUuidIndexID = context.MappedID(context.SourceUuidIndexID(), 0);
			}
			catch (com.db4o.CorruptionException exc)
			{
				j4o.lang.JavaSystem.PrintStackTrace(exc);
			}
			finally
			{
				context.Close();
			}
			if (targetIdentityID > 0)
			{
				SetIdentity(config.OrigPath(), targetIdentityID, targetUuidIndexID);
			}
			else
			{
				listener.NotifyDefragmentInfo(new com.db4o.defragment.DefragmentInfo("No database identity found in original file."
					));
			}
		}

		private static void DefragUnindexed(com.db4o.defragment.DefragContextImpl context
			)
		{
			System.Collections.IEnumerator unindexedIDs = context.UnindexedIDs();
			while (unindexedIDs.MoveNext())
			{
				int origID = ((int)unindexedIDs.Current);
				com.db4o.@internal.ReaderPair.ProcessCopy(context, origID, new _AnonymousInnerClass154
					(), true);
			}
		}

		private sealed class _AnonymousInnerClass154 : com.db4o.@internal.SlotCopyHandler
		{
			public _AnonymousInnerClass154()
			{
			}

			public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
			{
				com.db4o.@internal.ClassMetadata.DefragObject(readers);
			}
		}

		private static void SetIdentity(string targetFile, int targetIdentityID, int targetUuidIndexID
			)
		{
			com.db4o.@internal.LocalObjectContainer targetDB = (com.db4o.@internal.LocalObjectContainer
				)com.db4o.Db4o.OpenFile(com.db4o.defragment.DefragmentConfig.VanillaDb4oConfig()
				, targetFile);
			try
			{
				com.db4o.ext.Db4oDatabase identity = (com.db4o.ext.Db4oDatabase)targetDB.GetByID(
					targetIdentityID);
				targetDB.SetIdentity(identity);
				targetDB.SystemData().UuidIndexId(targetUuidIndexID);
			}
			finally
			{
				targetDB.Close();
			}
		}

		private static void FirstPass(com.db4o.defragment.DefragContextImpl context, com.db4o.defragment.DefragmentConfig
			 config)
		{
			Pass(context, config, new com.db4o.defragment.FirstPassCommand());
		}

		private static void SecondPass(com.db4o.defragment.DefragContextImpl context, com.db4o.defragment.DefragmentConfig
			 config)
		{
			Pass(context, config, new com.db4o.defragment.SecondPassCommand(config.ObjectCommitFrequency
				()));
		}

		private static void Pass(com.db4o.defragment.DefragContextImpl context, com.db4o.defragment.DefragmentConfig
			 config, com.db4o.defragment.PassCommand command)
		{
			command.ProcessClassCollection(context);
			com.db4o.ext.StoredClass[] classes = context.StoredClasses(com.db4o.defragment.DefragContextImpl
				.SOURCEDB);
			for (int classIdx = 0; classIdx < classes.Length; classIdx++)
			{
				com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)classes
					[classIdx];
				if (!config.StoredClassFilter().Accept(yapClass))
				{
					continue;
				}
				ProcessYapClass(context, yapClass, command);
				command.Flush(context);
				if (config.ObjectCommitFrequency() > 0)
				{
					context.TargetCommit();
				}
			}
			com.db4o.@internal.btree.BTree uuidIndex = context.SourceUuidIndex();
			if (uuidIndex != null)
			{
				command.ProcessBTree(context, uuidIndex);
			}
			command.Flush(context);
			context.TargetCommit();
		}

		private static void ProcessYapClass(com.db4o.defragment.DefragContextImpl context
			, com.db4o.@internal.ClassMetadata curClass, com.db4o.defragment.PassCommand command
			)
		{
			ProcessClassIndex(context, curClass, command);
			if (!ParentHasIndex(curClass))
			{
				ProcessObjectsForYapClass(context, curClass, command);
			}
			ProcessYapClassAndFieldIndices(context, curClass, command);
		}

		private static bool ParentHasIndex(com.db4o.@internal.ClassMetadata curClass)
		{
			com.db4o.@internal.ClassMetadata parentClass = curClass.i_ancestor;
			while (parentClass != null)
			{
				if (parentClass.HasIndex())
				{
					return true;
				}
				parentClass = parentClass.i_ancestor;
			}
			return false;
		}

		private static void ProcessObjectsForYapClass(com.db4o.defragment.DefragContextImpl
			 context, com.db4o.@internal.ClassMetadata curClass, com.db4o.defragment.PassCommand
			 command)
		{
			bool withStringIndex = WithFieldIndex(curClass);
			context.TraverseAll(curClass, new _AnonymousInnerClass247(command, context, curClass
				, withStringIndex));
		}

		private sealed class _AnonymousInnerClass247 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass247(com.db4o.defragment.PassCommand command, com.db4o.defragment.DefragContextImpl
				 context, com.db4o.@internal.ClassMetadata curClass, bool withStringIndex)
			{
				this.command = command;
				this.context = context;
				this.curClass = curClass;
				this.withStringIndex = withStringIndex;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				try
				{
					command.ProcessObjectSlot(context, curClass, id, withStringIndex);
				}
				catch (com.db4o.CorruptionException e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}

			private readonly com.db4o.defragment.PassCommand command;

			private readonly com.db4o.defragment.DefragContextImpl context;

			private readonly com.db4o.@internal.ClassMetadata curClass;

			private readonly bool withStringIndex;
		}

		private static bool WithFieldIndex(com.db4o.@internal.ClassMetadata clazz)
		{
			System.Collections.IEnumerator fieldIter = clazz.Fields();
			while (fieldIter.MoveNext())
			{
				com.db4o.@internal.FieldMetadata curField = (com.db4o.@internal.FieldMetadata)fieldIter
					.Current;
				if (curField.HasIndex() && (curField.GetHandler() is com.db4o.@internal.handlers.StringHandler
					))
				{
					return true;
				}
			}
			return false;
		}

		private static void ProcessYapClassAndFieldIndices(com.db4o.defragment.DefragContextImpl
			 context, com.db4o.@internal.ClassMetadata curClass, com.db4o.defragment.PassCommand
			 command)
		{
			int sourceClassIndexID = 0;
			int targetClassIndexID = 0;
			if (curClass.HasIndex())
			{
				sourceClassIndexID = curClass.Index().Id();
				targetClassIndexID = context.MappedID(sourceClassIndexID, -1);
			}
			command.ProcessClass(context, curClass, curClass.GetID(), targetClassIndexID);
		}

		private static void ProcessClassIndex(com.db4o.defragment.DefragContextImpl context
			, com.db4o.@internal.ClassMetadata curClass, com.db4o.defragment.PassCommand command
			)
		{
			if (curClass.HasIndex())
			{
				com.db4o.@internal.classindex.BTreeClassIndexStrategy indexStrategy = (com.db4o.@internal.classindex.BTreeClassIndexStrategy
					)curClass.Index();
				com.db4o.@internal.btree.BTree btree = indexStrategy.Btree();
				command.ProcessBTree(context, btree);
			}
		}

		internal class NullListener : com.db4o.defragment.DefragmentListener
		{
			public virtual void NotifyDefragmentInfo(com.db4o.defragment.DefragmentInfo info)
			{
			}
		}
	}
}
