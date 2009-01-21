using System.Collections.Generic;
using System.Linq;

namespace WixBuilder
{
	public class FileFinder
	{
		private readonly IFolder _root;

		public FileFinder(IFolder root)
		{
			_root = root;
		}

		public IEnumerable<IFile> FindAll(InclusionPattern pattern)
		{
			return from file in AllFilesStartingAt(_root) where pattern.Matches(file) select file;
		}

		private static IEnumerable<IFile> AllFilesStartingAt(IFolder root)
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
				foreach (var subFile in AllFilesStartingAt(folder))
				{
					yield return subFile;
				}
			}
		}
	}
}
