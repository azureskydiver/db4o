using System.Collections.Generic;

namespace WixBuilder
{
	public interface IFileSystemItem
	{
		string Name { get; }
		IFolder Parent { get; }
		string FullPath { get; }
		string ShortPathName { get; }
	}

	public interface IFile : IFileSystemItem
	{
	}

	public interface IFolder : IFileSystemItem
	{
		IEnumerable<IFileSystemItem> Children { get; }
		IFileSystemItem this[string name] { get; }
	}

	public interface IFolderBuilder
	{
		string FullPath { get;  }
		IFolderBuilder AddFiles(params string[] fileNames);
		IFolderBuilder EnterFolder(string folderName);
		IFolderBuilder LeaveFolder();
		IFolder GetFolder();
	}

	public static class FileSystemExtensions
	{
		public static IEnumerable<IFile> GetAllFilesRecursively(this IFolder root)
		{
			foreach (var item in root.Children)
			{
				IFile file = item as IFile;
				if (null != file)
				{
					yield return file;
					continue;
				}

				IFolder folder = (IFolder)item;
				foreach (var subFile in GetAllFilesRecursively(folder))
				{
					yield return subFile;
				}
			}
		}
	}
}
