/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class IxTraverser{

    private IxPath i_appendHead;
    private IxPath i_appendTail;

    private IxPath i_greatHead;
    private IxPath i_greatTail;

    YapDataType i_handler;

    private IxPath i_smallHead;
    private IxPath i_smallTail;

    // Bitmap that denotes, which elements to take, consisting of four booleans:
    // [0] take smaller
    // [1] take equal
    // [2] take greater
    // [3] take nulls 
    boolean[] i_take;
    
    Tree i_tree;

    private void add(Visitor4 visitor, IxPath a_previousPath, IxPath a_great, IxPath a_small) {
        addPathTree(visitor, a_previousPath);
        if (a_great != null && a_small != null && a_great.carriesTheSame(a_small)) {
            add(visitor, a_great, a_great.i_next, a_small.i_next);
            return;
        }
        addGreater(visitor, a_small);
        addSmaller(visitor, a_great);
    }
    

    private void addAll(Visitor4 visitor, Tree a_tree){
        if(a_tree != null){
            ((IxTree)a_tree).visit(visitor, null);
            addAll(visitor, a_tree.i_preceding);
            addAll(visitor, a_tree.i_subsequent);
        }
    }

    private void addGreater(Visitor4 visitor, IxPath a_path) {
        if (a_path != null) {
            if (a_path.i_next == null) {
                addSubsequent(visitor, a_path);
            } else {
                if (a_path.i_next.i_tree == a_path.i_tree.i_preceding) {
                    addSubsequent(visitor, a_path);
                } else {
                    addPathTree(visitor, a_path);
                }
                addGreater(visitor, a_path.i_next);
            }
        }
    }

    private void addPathTree(Visitor4 visitor, IxPath a_path) {
        if (a_path != null) {
            a_path.add(visitor);
        }
    }

    private void addPreceding(Visitor4 visitor, IxPath a_path) {
        addPathTree(visitor, a_path);
        addAll(visitor, a_path.i_tree.i_preceding);
    }

    private void addSmaller(Visitor4 visitor, IxPath a_path) {
        if (a_path != null) {
            if (a_path.i_next == null) {
                addPreceding(visitor, a_path);
            } else {
                if (a_path.i_next.i_tree == a_path.i_tree.i_subsequent) {
                    addPreceding(visitor, a_path);
                } else {
                    addPathTree(visitor, a_path);
                }
                addSmaller(visitor, a_path.i_next);
            }
        }
    }

    private void addSubsequent(Visitor4 visitor, IxPath a_path) {
        addPathTree(visitor, a_path);
        addAll(visitor, a_path.i_tree.i_subsequent);
    }

    private int countGreater(IxPath a_path, int a_sum) {
        if (a_path.i_next == null) {
            return a_sum + countSubsequent(a_path);
        } else {
            if (a_path.i_next.i_tree == a_path.i_tree.i_preceding) {
                a_sum += countSubsequent(a_path);
            } else {
                a_sum += a_path.countMatching();
            }
            return countGreater(a_path.i_next, a_sum);
        }
    }
    
    private int countPreceding(IxPath a_path) {
        return Tree.size(a_path.i_tree.i_preceding) + a_path.countMatching();
    }
    
    private int countSmaller(IxPath a_path, int a_sum) {
        if (a_path.i_next == null) {
            return a_sum + countPreceding(a_path);
        } else {
            if (a_path.i_next.i_tree == a_path.i_tree.i_subsequent) {
                a_sum += countPreceding(a_path);
            } else {
                a_sum += a_path.countMatching();
            }
            return countSmaller(a_path.i_next, a_sum);
        }
    }

    private int countSpan(IxPath a_previousPath, IxPath a_great, IxPath a_small) {
        if (a_great == null) {
            if (a_small == null) {
                return a_previousPath.countMatching();
            } else {
                return countGreater(a_small, a_previousPath.countMatching());
            }
        } else if (a_small == null) {
            return countSmaller(a_great, a_previousPath.countMatching());
        }
        if (a_great.carriesTheSame(a_small)) {
            return countSpan(a_great, a_great.i_next, a_small.i_next);
        }
        return a_previousPath.countMatching() + countGreater(a_small, 0) + countSmaller(a_great, 0);
    }

    private int countSubsequent(IxPath a_path) {
        return Tree.size(a_path.i_tree.i_subsequent) + a_path.countMatching();
    }

    private void delayedAppend(IxTree a_tree, int a_comparisonResult, int[] lowerAndUpperMatch) {
        if (i_appendHead == null) {
            i_appendHead = new IxPath(this, null, a_tree, a_comparisonResult, lowerAndUpperMatch);
            i_appendTail = i_appendHead;
        } else {
            i_appendTail = i_appendTail.append(a_tree, a_comparisonResult, lowerAndUpperMatch);
        }
    }

    private void findBoth() {
        if (i_greatTail.i_comparisonResult == 0) {
            findSmallestEqualFromEqual((IxTree)i_greatTail.i_tree.i_preceding);
            resetDelayedAppend();
            findGreatestEqualFromEqual((IxTree)i_greatTail.i_tree.i_subsequent);
        } else if (i_greatTail.i_comparisonResult < 0) {
            findBoth1((IxTree)i_greatTail.i_tree.i_subsequent);
        } else {
            findBoth1((IxTree)i_greatTail.i_tree.i_preceding);
        }
    }

    private void findBoth1(IxTree a_tree) {
        if (a_tree != null) {
            int res = a_tree.compare(null);
            int[] lowerAndUpperMatch = a_tree.lowerAndUpperMatch();
            i_greatTail = i_greatTail.append(a_tree, res, lowerAndUpperMatch);
            i_smallTail = i_smallTail.append(a_tree, res, lowerAndUpperMatch);
            findBoth();
        }
    }

    private int findBounds1(Object a_constraint, IxTree a_tree) {

        if (a_tree != null) {

            i_handler = a_tree.handler();
            i_handler.prepareComparison(a_constraint);

            // TODO: Only use small or big path where necessary.

            int res = a_tree.compare(null);
            
            i_greatHead = new IxPath(this, null, a_tree, res, a_tree.lowerAndUpperMatch());
            i_greatTail = i_greatHead;
            
            i_smallHead = i_greatHead.shallowClone();
            i_smallTail = i_smallHead;

            findBoth();

            int span = 0;

            if (i_take[1]) {
                span += countSpan(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
            }
            if (i_take[0]) {
                IxPath head = i_smallHead;
                while (head != null) {
                    span += head.countPreceding(i_take[3]);
                    head = head.i_next;
                }
            }
            if (i_take[2]) {
                IxPath head = i_greatHead;
                while (head != null) {
                    span += head.countSubsequent();
                    head = head.i_next;
                }
            }
            return span;
        }
        return 0;
    }
    
    

    public int findBoundsExactMatch(Object a_constraint, IxTree a_tree){
        i_take = new boolean[] { false, true, false, false};
        return findBounds1(a_constraint, a_tree);
    }
    
    public int findBoundsQuery(QCon a_qcon, Object constraint) {
        if (!a_qcon.i_evaluator.supportsIndex()) {
            return -1;
        }
        i_take = new boolean[] { false, false, false, false};
        a_qcon.i_evaluator.indexBitMap(i_take);
        return findBounds1(constraint, a_qcon.indexRoot());
    }
    
    private void findGreatestEqual(IxTree a_tree) {
        int res = a_tree.compare(null);
        i_greatTail = i_greatTail.append(a_tree, res, a_tree.lowerAndUpperMatch());
        if (res == 0) {
            findGreatestEqualFromEqual(a_tree);
        } else if (res < 0) {
            if (a_tree.i_subsequent != null) {
                findGreatestEqual((IxTree)a_tree.i_subsequent);
            }
        } else {
            if (a_tree.i_preceding != null) {
                findGreatestEqual((IxTree)a_tree.i_preceding);
            }
        }
    }
    
    private void findGreatestEqualFromEqual(IxTree a_tree) {
        if (a_tree != null) {
            int res = a_tree.compare(null);
            delayedAppend(a_tree, res, a_tree.lowerAndUpperMatch());
            if (res == 0) {
                i_greatTail = i_greatTail.append(i_appendHead, i_appendTail);
                resetDelayedAppend();
            }
            if (res > 0) {
                findGreatestEqualFromEqual((IxTree)a_tree.i_preceding);
            } else {
                findGreatestEqualFromEqual((IxTree)a_tree.i_subsequent);
            }
        }
    }
    
    private void findSmallestEqual(IxTree a_tree) {
        int res = a_tree.compare(null);
        i_smallTail = i_smallTail.append(a_tree, res, a_tree.lowerAndUpperMatch());
        if (res == 0) {
            findSmallestEqualFromEqual(a_tree);
        } else if (res < 0) {
            if (a_tree.i_subsequent != null) {
                findSmallestEqual((IxTree)a_tree.i_subsequent);
            }
        } else {
            if (a_tree.i_preceding != null) {
                findSmallestEqual((IxTree)a_tree.i_preceding);
            }
        }
    }
    
    private void findSmallestEqualFromEqual(IxTree a_tree) {
        if (a_tree != null) {
            int res = a_tree.compare(null);
            delayedAppend(a_tree, res, a_tree.lowerAndUpperMatch());
            if (res == 0) {
                i_smallTail = i_smallTail.append(i_appendHead, i_appendTail);
                resetDelayedAppend();
            }
            if (res < 0) {
                findSmallestEqualFromEqual((IxTree)a_tree.i_subsequent);
            } else {
                findSmallestEqualFromEqual((IxTree)a_tree.i_preceding);
            }
        }
    }
    
    private void resetDelayedAppend() {
        i_appendHead = null;
        i_appendTail = null;
    }
    
    public void visitAll(Visitor4 visitor) {
        i_tree = null;
        if (i_take[1]) {
            if (i_greatHead != null) {
                add(visitor, i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
            }
        }
        if (i_take[0]) {
            IxPath head = i_smallHead;
            while (head != null) {
                head.addPrecedingToCandidatesTree(visitor);
                head = head.i_next;
            }
        }
        if (i_take[2]) {
            IxPath head = i_greatHead;
            while (head != null) {
                head.addSubsequentToCandidatesTree(visitor);
                head = head.i_next;
            }
        }
    }


}
