using System;
using System.Management.Automation;
using Db4objects.Db4o;

namespace CmdLets.Db4objects
{
    [Cmdlet(VerbsCommon.Add , "db4o-object")]
    public class AddDb4oObjectCommand : Db4oObjectCommandBase
    {
        protected override void ProcessRecord()
        {
            using (var container = Db4oEmbedded.OpenFile(Configure(), DatabasePath))
            {
                if (null != Item)
                {
                    container.Store(Item);

                    // Name+Country+Age / Name == "Adriano" || Country == "Brazil"
                    // 
                    // class Name_Country_Syntethic_1
                    // {
                    //      string Name;
                    //      string Country; 
                    //      int Age;
                    // }
                    //
                    // from Name_Country_Syntethic_1 obj in db()
                    // where obj.Name == "Adriano" || obj.Country == "Brazil"
                    // select obj;
                }
            }
        }

        [Parameter(Position = 0)]
        public PSObject Item
        {
            get; set;
        }
    }

    public class Wrapper
    {
        private DateTime _date;
        private int _value;

        public Wrapper(DateTime date)
        {
            _date = date;
            _value = new Random(date.Millisecond).Next();
        }

        public int Value
        {
            get { return _value;}
            set { _value = value;}
        }

        public DateTime Date
        {
            get { return _date; }
            set { _date = value; }
        }

        public override string ToString()
        {
            return "Wrapper: " + _date;
        }
    }
}
