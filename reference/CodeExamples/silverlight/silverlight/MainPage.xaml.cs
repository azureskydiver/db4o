using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Db4objects.Db4o;
using Db4oDoc.Silverlight.Model;

namespace silverlight
{
    public partial class MainPage : UserControl
    {
        private IObjectContainer container;
        private ObservableCollection<Person> persons = new ObservableCollection<Person>();

        public MainPage()
        {
            InitializeComponent();
            this.DataContext = persons;
        }

        public MainPage(IObjectContainer container):this()
        {
            this.container = container; 
        }

        private void storeButton_Click(object sender, RoutedEventArgs e)
        {
            container.Store(new Person(){FirstName = "Roman",SirName = "Stoffel"});
        }

        private void queryData(object sender, RoutedEventArgs e)
        {
            persons.Clear();
            var query = container.Query();
            query.Constrain(typeof (Person));
            query.Descend("SirName").Constrain("Sto").Contains();
            foreach (Person person in query.Execute())
            {
                persons.Add(person);    
            }
        }

        public ObservableCollection<Person> Persons
        {
            get { return persons; }
        }
    }
}
