/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using com.db4o.query;

namespace com.db4o.test
{
    public class MaxByEvaluation
    {
        static int MAX = 20;

        public int val;

        public void store()
        {
            for(int i = 0; i <= MAX; i++)
            {
                MaxByEvaluation mbe = new MaxByEvaluation();
                mbe.val = i;
                Test.store(mbe);
            }
        }

        public void test()
        {
            Query q = Test.query();
            q.constrain(typeof(MaxByEvaluation));
            q.descend("val").constrain(new EvalCallbackForMax());
            ObjectSet objectSet = q.execute();
            Test.ensure(objectSet.size() == 1);
            MaxByEvaluation mbe = (MaxByEvaluation)objectSet.next();
            Test.ensure(mbe.val == MAX);
        }
    }

    public class EvalCallbackForMax : Evaluation
    {
        private int currentMax;
        private Candidate currentCandidate;

        public void evaluate(Candidate candidate)
        {
            int current = (int)candidate.getObject();
            if(currentCandidate != null)
            {
                if(currentMax > current)
                {
                    candidate.include(false);
                    return;
                }
                currentCandidate.include(false);
            }
            candidate.include(true);
            currentCandidate = candidate;
            currentMax = current;
        }
    }
}
