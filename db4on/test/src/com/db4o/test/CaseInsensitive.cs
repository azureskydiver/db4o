using System;

using com.db4o.query;

namespace com.db4o.test {

    /**
     * demonstrates a case-insensitive query using an Evaluation
     */
    public class CaseInsensitive {
    
        public String name;
    
        public CaseInsensitive() {
        }
    
        public CaseInsensitive(String name) {
            this.name = name;
        }
    
        public void store(){
            Tester.store(new CaseInsensitive("HelloWorld"));
        }
    
        public void test(){
            Tester.ensure(queryingCaseInsensitiveResults("heLLOworld") == 1);
        }
    
        private int queryingCaseInsensitiveResults(string name){
            ObjectContainer objectContainer = Tester.objectContainer();
            Query q = objectContainer.query();
            q.constrain(typeof(CaseInsensitive));
            q.constrain(new CaseInsensitiveEvaluation(name));
            return q.execute().size();
        }
    }


    public class CaseInsensitiveEvaluation: Evaluation {

        String name;

        public CaseInsensitiveEvaluation(String name){
            this.name = name;
        }

        public void evaluate(Candidate candidate) {
            CaseInsensitive ci = (CaseInsensitive)candidate.getObject();
            candidate.include(ci.name.ToLower().Equals(name.ToLower()));
        }
    }
}
