/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.CommitCallbacks
{
    public class Item
    {
        private int _number;
        private string _word;

        public Item(int number, string word)
        {
            _number = number;
            _word = word;
        }

        public string Word
        {
            get
            {
                return _word;
            }
        }

        public int Number
        {
            get
            {
                return _number;
            }
        }

        public override string ToString()
        {
            return _number + "/" + _word;
        }
    }
}
