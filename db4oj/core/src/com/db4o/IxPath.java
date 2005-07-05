/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Index Path to represent a list of traversed index tree entries,
 * used by IxTraverser
 */
class IxPath implements Cloneable, Visitor4 {

    private QCandidates i_candidates;

    int                 i_comparisonResult;

    int[]               i_lowerAndUpperMatch;
    int                 i_upperNull = -1;

    IxPath              i_next;

    IxTraverser         i_traverser;
    IxTree              i_tree;
    
    Visitor4            _visitor;

    IxPath(IxTraverser a_traverser, IxPath a_next, IxTree a_tree,
        int a_comparisonResult, int[] lowerAndUpperMatch) {
        i_traverser = a_traverser;
        i_next = a_next;
        i_tree = a_tree;
        i_comparisonResult = a_comparisonResult;
        i_lowerAndUpperMatch = lowerAndUpperMatch;
    }
    
    void add(Visitor4 visitor) {
        if (i_comparisonResult == 0 && i_traverser.i_take[1]) {
            i_tree.visit(visitor, i_lowerAndUpperMatch);
        }
    }

    void addPrecedingToCandidatesTree(Visitor4 visitor) {
        _visitor = visitor;
        if (i_tree.i_preceding != null) {
            if (i_next == null || i_next.i_tree != i_tree.i_preceding) {
                i_tree.i_preceding.traverse(this);
            }
        }
        if (i_lowerAndUpperMatch != null) {
            int[] lowerAndUpperMatch = new int[] { i_upperNull,
                i_lowerAndUpperMatch[0] - 1};
            i_tree.visit(visitor, lowerAndUpperMatch);
        } else {
            if (i_comparisonResult < 0) {
                visit(i_tree);
            }
        }
    }

    void addSubsequentToCandidatesTree(Visitor4 visitor) {
        _visitor = visitor;
        if (i_tree.i_subsequent != null) {
            if (i_next == null || i_next.i_tree != i_tree.i_subsequent) {
                i_tree.i_subsequent.traverse(this);
            }
        }
        if (i_lowerAndUpperMatch != null) {
            int[] lowerAndUpperMatch = new int[] { i_lowerAndUpperMatch[1] + 1,
                ((IxFileRange) i_tree)._entries - 1};
            i_tree.visit(visitor, lowerAndUpperMatch);
        } else {
            if (i_comparisonResult > 0) {
                visit(i_tree);
            }
        }
    }

    IxPath append(IxPath a_head, IxPath a_tail) {
        if (a_head == null) {
            return this;
        }
        i_next = a_head;
        return a_tail;
    }

    IxPath append(IxTree a_tree, int a_comparisonResult, int[] lowerAndUpperMatch) {
        i_next = new IxPath(i_traverser, null, a_tree, a_comparisonResult, lowerAndUpperMatch);
        i_next.i_tree = a_tree;
        return i_next;
    }

    boolean carriesTheSame(IxPath a_path) {
        return i_tree == a_path.i_tree;
    }

    private void checkUpperNull() {
        if (i_upperNull == -1) {
            i_upperNull = 0;
            i_traverser.i_handler.prepareComparison(null);
            int res = i_tree.compare(null);
            if(res != 0){
                return;
            }
            int[] nullMatches = i_tree.lowerAndUpperMatch();  
            if (nullMatches[0] == 0) {
                i_upperNull = nullMatches[1] + 1;
            } else {
            	i_upperNull = 0; 
            }
        }
    }

    int countMatching() {
        if (i_comparisonResult == 0) {
            if (i_lowerAndUpperMatch == null) {
                if (i_tree instanceof IxRemove) {
                    return 0;
                }
                return 1;
            }
            return i_lowerAndUpperMatch[1] - i_lowerAndUpperMatch[0] + 1;
        }
        return 0;
    }

    int countPreceding(boolean a_takenulls) {
        int preceding = 0;
        if (i_tree.i_preceding != null) {
            if (i_next == null || i_next.i_tree != i_tree.i_preceding) {
                preceding += i_tree.i_preceding.size();
            }
        }
        if (i_lowerAndUpperMatch != null) {
            if(a_takenulls) {
                i_upperNull = 0;
            }else {
                checkUpperNull();
            }
            preceding += i_lowerAndUpperMatch[0] - i_upperNull;
        } else {
            if (i_comparisonResult < 0 && !(i_tree instanceof IxRemove)) {
                preceding++;
            }
        }
        return preceding;
    }

    int countSubsequent() {
        int subsequent = 0;
        if (i_tree.i_subsequent != null) {
            if (i_next == null || i_next.i_tree != i_tree.i_subsequent) {
                subsequent += i_tree.i_subsequent.size();
            }
        }
        if (i_lowerAndUpperMatch != null) {
            subsequent += ((IxFileRange) i_tree)._entries
                - i_lowerAndUpperMatch[1] - 1;
        } else {
            if (i_comparisonResult > 0 && !(i_tree instanceof IxRemove)) {
                subsequent++;
            }
        }
        return subsequent;
    }

    IxPath shallowClone() {
        try {
            return (IxPath) this.clone();
        } catch (CloneNotSupportedException e) {

        }
        return null;
    }

    public String toString() {
        return i_tree.toString();
    }

    public void visit(Object a_object) {
        ((IxTree) a_object).visit(_visitor, null);
    }
    



}