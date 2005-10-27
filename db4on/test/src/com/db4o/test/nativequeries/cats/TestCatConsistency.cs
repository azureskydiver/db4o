using System;
using System.Collections;
using com.db4o.ext;
using com.db4o.test;
using com.db4o.query;

namespace com.db4o.test.nativequeries.cats
{
	public class TestCatConsistency
	{
        public void configure(){
            Db4o.configure().optimizeNativeQueries(false);
        }
        
        public void store(){
            storeCats();
        }
        
        public void test(){
            
            ExtObjectContainer oc = Tester.objectContainer();
            oc.configure().optimizeNativeQueries(true);
            runTests();
            oc.configure().optimizeNativeQueries(false);
            runTests();

        }

        public class NoneFound : Predicate {
            public bool match(Cat cat) {
                return cat._age == 7;
            }
        }

        public class AgeOne : Predicate {
            public bool match(Cat cat) {
                return cat._age == 1;
            }
        }

        public class FatherAgeOne : Predicate {
            public bool match(Cat cat) {
                return cat._father._age == 1;
            }
        }

        public class GrandFatherName : Predicate {
            public bool match(Cat cat) {
                return cat._father._father._firstName == "Edwin";
            }
        }

        public class OrFatherName : Predicate {
            public bool match(Cat cat) {
                return cat._father._father._firstName == "Edwin"
                    || cat._father._firstName == "Edwin";
            }
        }

        public class AddToAge : Predicate {
            public bool match(Cat cat) {
                return cat._age + 1 == 2;
            }
        }

        public class TwoGetters : Predicate {
            public bool match(Cat cat) {
                return cat.getFirstName() == "Occam"
                    && cat.getAge() == 1;
            }
        }

        public class CalculatedGetter : Predicate {
            public bool match(Cat cat) {
                return cat.getFullName() == "Achat Leo Lenis";
            }
        }

        public class GetterNull : Predicate {
            public bool match(Cat cat) {
                return cat.getFullName() == null;
            }
        }

        public class StartsWith : Predicate {
            public bool match(Cat cat) {
                return cat._firstName.StartsWith("A");
            }
        }


        
        
        public void runTests(){
            
            expect(new NoneFound(), null);
            
            expect(new AgeOne(), new String[]{"Occam", "Vahiné" });
            
            expect(new FatherAgeOne(),new String[]{"Achat", "Acrobat" });
            
            expect(new GrandFatherName(), new String[]{"Achat", "Acrobat" });
            
            expect(new OrFatherName(), new String[]{"Achat", "Acrobat"});
            
            expect(new AddToAge(), new String[]{"Occam", "Vahiné" });
            
            expect(new TwoGetters(), new String[]{"Occam"});
            
            expect(new CalculatedGetter(), new String[]{"Achat"});

            expect(new GetterNull(), new String[]{});
            
            expect(new StartsWith(), new String[]{"Achat", "Acrobat"});
            
        }
        
        public void storeCats(){
            
            Cat winni = new Cat();
            winni._sex = Animal.MALE;
            winni._firstName = "Edwin";
            winni._lastName = "Sanddrops";
            winni._age = 12;
            
            Cat bachi = new Cat();
            bachi._sex = Animal.FEMALE;
            bachi._firstName = "Frau Bachmann";
            bachi._lastName = "von der Bärenhöhle";
            bachi._age = 10;
            
            Cat occam = new Cat();
            occam._sex = Animal.MALE;
            occam._firstName = "Occam";
            occam._lastName = "von der Bärenhöhle";
            occam._age = 1;
            occam._father = winni;
            occam._mother = bachi;
            
            Cat zora = new Cat();
            zora._sex = Animal.FEMALE;
            zora._firstName = "Vahiné";
            zora._lastName = "des Fauves et Or";
            zora._age = 1;
            
            Cat achat = new Cat();
            achat._sex = Animal.FEMALE;
            achat._firstName = "Achat";
            achat._lastName = "Leo Lenis";
            achat._father = occam;
            achat._mother = zora;
            
            Cat acrobat = new Cat();
            acrobat._sex = Animal.FEMALE;
            acrobat._firstName = "Acrobat";
            acrobat._lastName = "Leo Lenis";
            acrobat._father = occam;
            acrobat._mother = zora;
            
            Tester.store(achat);
            Tester.store(acrobat);
            
            Cat trulla = new Cat();
            trulla._firstName = "Trulla";
            
        }
        
        private void expect(Predicate predicate, String[] names){
            
            if(names == null){
                names = new String[] {};
            }
            
            IList list = Tester.objectContainer().query(predicate);

            IEnumerator i = list.GetEnumerator();
            
            while(i.MoveNext()){
                Cat cat = (Cat)i.Current;
                bool good = false;
                for (int j = 0; j < names.Length; j++) {
                    if(names[j] != null){
                        if(cat._firstName.Equals(names[j])){
                            names[j] = null;
                            good = true;
                            break;
                        }
                    }
                }
                Tester.ensure(good);
            }
            for (int j = 0; j < names.Length; j++) {
                Tester.ensure(names[j] == null);
            }
        }
        
        
    
	}
}
