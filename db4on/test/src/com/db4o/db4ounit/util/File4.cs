using System.IO;

namespace com.db4o.db4ounit.util
{
    public class File4
    {
        public static void Delete(string file)
        {
            if (File.Exists(file))
            {
                File.Delete(file);
            }
        }

        public static void Copy(string from, string to)
        {
            File.Copy(from, to, true);
        }
    }
}
