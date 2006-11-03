/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using com.db4o.query;

namespace com.db4o.test
{
    public class MaxByEvaluation
    {
        static int MAX = 20;

        public int val;

        public void Store()
        {
            for(int i = 0; i <= MAX; i++)
            {
                MaxByEvaluation mbe = new MaxByEvaluation();
                mbe.val = i;
                Tester.Store(mbe);
            }
        }

        public void Test()
        {
            Query q = Tester.Query();
            q.Constrain(typeof(MaxByEvaluation));
            q.Descend("val").Constrain(new EvalCallbackForMax());
            ObjectSet objectSet = q.Execute();
            while (objectSet.HasNext())
            {
                objectSet.Next();
            }
            Tester.EnsureEquals(1, objectSet.Size());
            MaxByEvaluation mbe = (MaxByEvaluation) objectSet[0];
            Tester.EnsureEquals(MAX, mbe.val);
        }
    }

    public class EvalCallbackForMax : Evaluation
    {
        private int currentMax;
        private Candidate currentCandidate;

        public void Evaluate(Candidate candidate)
        {
            int current = (int)candidate.GetObject();
            if(currentCandidate != null)
            {
                if(currentMax > current)
                {
                    candidate.Include(false);
                    return;
                }
                currentCandidate.Include(false);
            }
            candidate.Include(true);
            currentCandidate = candidate;
            currentMax = current;
        }
    }
}
