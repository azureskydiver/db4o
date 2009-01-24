using System.Text.RegularExpressions;

namespace WixBuilder
{
	public class InclusionPattern
	{
		private readonly string _pattern;

		public InclusionPattern(string pattern)
		{
			_pattern = pattern.Replace(@"\", @"\\").Replace(".", "\\.").Replace("*", @".*");
		}

		public bool Matches(IFile file)
		{
			return Regex.IsMatch(file.Path, _pattern);
		}
	}
}
