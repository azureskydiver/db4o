namespace CFNativeQueriesEnabler.Tests.Subject
{
    public class Item
    {
        private string _name;
        
        public Item(string name)
        {
            _name = name;
        }

        public string Name
        {
            get { return _name; }
        }
    }
}