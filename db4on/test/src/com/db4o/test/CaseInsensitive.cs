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
    
        public void Store(){
            Tester.Store(new CaseInsensitive("HelloWorld"));
        }
    
        public void Test(){
            Tester.Ensure(QueryingCaseInsensitiveResults("heLLOworld") == 1);
        }
    
        private int QueryingCaseInsensitiveResults(string name){
            ObjectContainer objectContainer = Tester.ObjectContainer();
            Query q = objectContainer.Query();
            q.Constrain(typeof(CaseInsensitive));
            q.Constrain(new CaseInsensitiveEvaluation(name));
            return q.Execute().Size();
        }
    }


    public class CaseInsensitiveEvaluation: Evaluation {

        String name;

        public CaseInsensitiveEvaluation(String name){
            this.name = name;
        }

        public void Evaluate(Candidate candidate) {
            CaseInsensitive ci = (CaseInsensitive)candidate.GetObject();
            candidate.Include(ci.name.ToLower().Equals(name.ToLower()));
        }
    }
}
