using System;
using System.Collections.Generic;
using System.Text;

namespace CFNativeQueriesEnabler.Tests.Subject
{
    class Assert
    {
        public static void AreEqual(object expected, object actual)
        {
            if (!object.Equals(expected, actual))
            {
                throw new ApplicationException(string.Format("'{0}' != '{1}'", expected, actual));
            }
        }
    }
}
