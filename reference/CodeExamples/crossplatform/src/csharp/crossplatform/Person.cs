
// #example: The person class in C-Sharp
namespace Db4odoc.CrossPlatform.CrossPlatform
{
    public class Person
    {
        private string firstname;
        private string sirname;

        public Person(string firstname, string sirname)
        {
            this.firstname = firstname;
            this.sirname = sirname;
        }

        public string Firstname
        {
            get { return firstname; }
            set { firstname = value; }
        }

        public string Sirname
        {
            get { return sirname; }
            set { sirname = value; }
        }

        public override string ToString()
        {
            return string.Format("Firstname: {0}, Sirname: {1}", firstname, sirname);
        }
    }
}
// #end example