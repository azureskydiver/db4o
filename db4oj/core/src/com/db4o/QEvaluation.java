/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

public class QEvaluation extends QCon {

    private transient Object i_evaluation;
    byte[] i_marshalledEvaluation;
    int i_marshalledID;
    boolean i_isDelegate;

    public QEvaluation() {
        // C/S only
    }

    QEvaluation(Transaction a_trans, Object a_evaluation, boolean a_isDelegate) {
        super(a_trans);
        i_evaluation = a_evaluation;
        i_isDelegate = a_isDelegate;
    }

    void evaluateEvaluationsExec(QCandidates a_candidates, boolean rereadObject) {
        if (rereadObject) {
            a_candidates.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ((QCandidate) a_object).useField(null);
                }
            });
        }
        a_candidates.filter(this);
    }

    void marshall() {
        super.marshall();
		int[] id = {0};
		if(Deploy.csharp){
		    i_marshalledEvaluation = i_trans.i_stream.marshall(i_evaluation, id);
		}else{
		    try{
		        // try serialisation. If it fails, store as db4o.
		        i_marshalledEvaluation = Platform.serialize(i_evaluation);
		    }catch (Exception e){
		        i_marshalledEvaluation = i_trans.i_stream.marshall(i_evaluation, id);
		    }
		    
		}
		i_marshalledID = id[0];
    }

    void unmarshall(Transaction a_trans) {
        if (i_trans == null) {
            super.unmarshall(a_trans);
            if(Deploy.csharp){
                i_evaluation = i_trans.i_stream.unmarshall(i_marshalledEvaluation, i_marshalledID);
            }else{
                if(i_marshalledID > 0){
                    i_evaluation = i_trans.i_stream.unmarshall(i_marshalledEvaluation, i_marshalledID);
                }else{
                    i_evaluation = Platform.deserialize(i_marshalledEvaluation);
                }
            }
        }
    }

    public void visit(Object obj) {
		QCandidate candidate = (QCandidate)obj;		
        try {
            Platform.evaluationEvaluate(i_evaluation, candidate);
            if(! candidate.i_include){
            	doNotInclude(candidate.getRoot());
            }
        } catch (Exception e) {
            candidate.include(false);
			doNotInclude(candidate.getRoot());
            // TODO: implement Exception callback for the user coder
            // at least for test cases
        }
    }
    
    boolean supportsIndex(){
        return false;
    }
}
