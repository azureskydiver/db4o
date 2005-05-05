namespace com.db4o.f1.chapter6
{
    using System.Globalization;

    /// <summary>
    /// A CultureInfo aware list of objects.
    /// CultureInfo objects hold a native pointer to 
    /// a system structure.
    /// </summary>
    public class LocalizedItemList
    {
        CultureInfo _culture;
        string[] _items;

        public LocalizedItemList(CultureInfo culture, string[] items)
        {
            _culture = culture;
            _items = items;
        }

        override public string ToString()
        {
            return string.Join(_culture.TextInfo.ListSeparator + " ", _items);
        }
    }
}