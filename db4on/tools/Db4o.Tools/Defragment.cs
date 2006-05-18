/* Copyright (C) 2004-2006   db4objects Inc.   http://www.db4o.com */

namespace Db4o.Tools {

	using System;

	using j4o.io;
	using j4o.lang;
	using j4o.lang.reflect;

	using com.db4o;
	using com.db4o.ext;
	using com.db4o.types;

	/**
    * defragments a database file. 
    * <br><br>This class is not part of db4o.jar. It is delivered as
    * sourcecode in the path ../com/db4o/tools/<br><br>
    * <b>Prerequites:</b><br>
    * - The database file may not be in use.<br>
    * - All stored classes need to be available.<br>
    * - If you use yor own special Db4o translators, they need to be
    * installed before starting Defragment.
    * <br><br>
    * <b>Performed tasks:</b><br>
    * - Free filespace is removed.<br>
    * - Deleted IDs are removed.<br>
    * - Unavailable classes are removed.<br>
    * - Unavailable class members are removed.<br>
    * - Class indices are restored.<br>
    * - Previous rename tasks are removed.<br>
    * <br>
    * <b>Backup:</b><br>
    * Defragment creates a backup file with the name [filename].bak.
    * If a file with this name is already present, Defragment will not run
    * for safety reasons.<br>
    * <br>
    * <b>Recommendations:</b><br>
    * - Keep the backup copy of your database file.<br>
    * - <b>Always</b> back up your class files with your database files also.<br>
    * You will need them to restore the full data of all objects from old
    * database file versions.<br>
    * - Scan the output log for "Class not available" messages.<br>
    * <br>
    * You may also run this task programmatically on a scheduled basis. In this
    * case note that <code>Defragment</code> modifies db4o configuration
    * parameters. You may have to restore them for your application. See the
    * private methods Defragment#configureDb4o() and Db4o#restoreConfiguration()
    * in the sourcecode of com.db4o.tools.Defragment.cs for the exact changed
    * parameters that may need to be restored.
    */
	public class Defragment
	{
		static readonly j4o.lang.Class ObjectClass = j4o.lang.Class.getClassForType(typeof(object));
		
		/**
       * the main method is the only entry point
       */
		public Defragment() : base()
		{
		}

		/**
       * the main method that runs Defragment. 
       * @param String[] a String array of length 1, with the name of
       * the database file as element 0.
       */
		public static void Main(String[] args)
		{
			if (args != null && args.Length > 0)
			{
				bool forceBackupDelete1 = args.Length > 1 && "!".Equals(args[1]);
				new Defragment().run(args[0], forceBackupDelete1);
			}
			else
			{
				Console.WriteLine("Usage: java com.db4o.tools.Defragment <database filename>");
			}
		}

		/**
       * programmatic interface to run Defragment with a forced delete of
       * a possible old Defragment backup. <br>
       * This method is supplied for regression tests only. It is not
       * recommended to be used by application programmers.
       * @param filename the database file.
       * @param forceBackupDelete forces deleting an old backup.
       * <b>Not recommended.</b>
       */
		public void run(String filename, bool forceBackupDelete)
		{
			File file = new File(filename);
			if (file.exists())
			{
				bool canRun = true;
				ExtFile backupTest = new ExtFile(file.getAbsolutePath() + ".bak");
				if (backupTest.exists())
				{
					if (forceBackupDelete)
					{
						backupTest.delete();
					}
					else
					{
						canRun = false;
						Console.WriteLine("A backup file with the name ");
						Console.WriteLine("\'" + backupTest.getAbsolutePath() + "\'");
						Console.WriteLine("already exists.");
						Console.WriteLine("Remove this file before calling \'Defragment\'.");
					}
				}
				if (canRun)
				{
					file.renameTo(backupTest);
					try
					{
						configureDb4o();
						ObjectContainer readFrom = Db4o.openFile(backupTest.getAbsolutePath());
						ObjectContainer writeTo = Db4o.openFile(file.getAbsolutePath());
						writeTo.ext().migrateFrom(readFrom);
						migrate(readFrom, writeTo);
						readFrom.close();
						writeTo.close();
						Console.WriteLine("Defragment operation completed successfully.");
					}
					catch (Exception e)
					{
						Console.WriteLine("Defragment operation failed.");
						j4o.lang.JavaSystem.printStackTrace(e);
						try
						{
							new File(filename).delete();
							backupTest.copy(filename);
						}
						catch (Exception ex)
						{
							Console.WriteLine("Restore failed.");
							Console.WriteLine("Please use the backup file:");
							Console.WriteLine("\'" + backupTest.getAbsolutePath() + "\'");
							return;
						}
						Console.WriteLine("The original file was restored.");
						try
						{
							new File(backupTest.getAbsolutePath()).delete();
						}
						catch (Exception ignored)
						{
						}
					}
					finally
					{
						restoreConfiguration();
					}
				}
			}
			else
			{
				Console.WriteLine("File \'" + file.getAbsolutePath() + "\' does not exist.");
			}
		}

		private void configureDb4o()
		{
			Db4o.configure().activationDepth(0);
			Db4o.configure().callbacks(false);
			Db4o.configure().classActivationDepthConfigurable(false);
			Db4o.configure().weakReferences(false);
		}

		private void restoreConfiguration()
		{
			Db4o.configure().activationDepth(5);
			Db4o.configure().callbacks(true);
			Db4o.configure().classActivationDepthConfigurable(true);
			Db4o.configure().weakReferences(true);
		}

		private void migrate(ObjectContainer origin, ObjectContainer destination)
		{
			StoredClass[] classes = origin.ext().storedClasses();
			filterAbstractSecondAndNotFoundClasses(classes);
			filterSubclasses(classes);
			migrateClasses(origin, destination, classes);
		}

		private static void migrateClasses(ObjectContainer origin, ObjectContainer destination, StoredClass[] classes)
		{
			for (int i = 0; i < classes.Length; i++)
			{
				if (classes[i] != null)
				{
					long[] ids = classes[i].getIDs();
					origin.ext().purge();
					destination.commit();
					destination.ext().purge();
					for (int j = 0; j < ids.Length; j++)
					{
						Object obj = origin.ext().getByID(ids[j]);
						origin.activate(obj, 1);
						origin.deactivate(obj, 2);
						origin.activate(obj, 3);
						destination.set(obj);
						origin.deactivate(obj, 1);
						destination.deactivate(obj, 1);
					}
				}
			}
		}

		/// <summary>
		/// Remove subclasses from the list since objects from subclasses will be
		/// returned by superclass.getIds()
		/// </summary>
		/// <param name="classes"></param>
		private static void filterSubclasses(StoredClass[] classes)
		{
			for (int i = 0; i < classes.Length; i++)
			{
				if (classes[i] == null)
				{
					continue;
				}

				Class javaClass = Class.forName(classes[i].getName());
				if (isSubclass(classes, javaClass))
				{
					classes[i] = null;
				}
			}
		}

		private static bool isSubclass(StoredClass[] classes, Class candidate)
		{
			for (int j = 0; j < classes.Length; j++)
			{
				if (classes[j] != null)
				{
					Class superClass1 = Class.forName(classes[j].getName());
					if (candidate != superClass1 && superClass1.isAssignableFrom(candidate))
					{
						return true;
					}
				}
			}
			return false;
		}

		private static void filterAbstractSecondAndNotFoundClasses(StoredClass[] classes)
		{
			for (int i = 0; i < classes.Length; i++)
			{
				try
				{
					Class javaClass = Class.forName(classes[i].getName());
					if (javaClass == null
						|| javaClass == ObjectClass
						|| isSecondClass(javaClass)
						|| Modifier.isAbstract(javaClass.getModifiers()))
					{
						classes[i] = null;
					}
				}
				catch (ClassNotFoundException e)
				{
					classes[i] = null;
				}
			}
		}

		private static bool isSecondClass(Class javaClass)
		{
			return Class.getClassForType(typeof(SecondClass)).isAssignableFrom(javaClass);
		}

		private class ExtFile : File
		{
			public ExtFile(String path) : base(path)
			{
			}

			public ExtFile copy(String toPath)
			{
				try
				{
					{
						new ExtFile(toPath).mkdirs();
						new ExtFile(toPath).delete();
						int bufferSize1 = 64000;
						RandomAccessFile rafIn = new RandomAccessFile(getAbsolutePath(), "r");
						RandomAccessFile rafOut = new RandomAccessFile(toPath, "rw");
						long len = rafIn.length();
						byte[] bytes = new byte[bufferSize1];
						while (len > 0)
						{
							len -= bufferSize1;
							if (len < 0)
							{
								bytes = new byte[(int) (len + bufferSize1)];
							}
							rafIn.read(bytes);
							rafOut.write(bytes);
						}
						rafIn.close();
						rafOut.close();
						return new ExtFile(toPath);
					}
				}
				catch (Exception e)
				{
					{
						j4o.lang.JavaSystem.printStackTrace(e);
						throw e;
					}
				}
			}
		}
	}
}
