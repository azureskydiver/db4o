
using System;
using System.Windows.Forms;
using Db4oDoc.Code.Concurrency.Transactions;
using Db4oDoc.Code.Indexing.Traverse;
using Db4oDoc.Code.Reporting;

namespace Db4oDoc
{
    public class Boot
    {
        [STAThread]
        public static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new ReportForm());
        }
    }
}