/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Index traverser
 */
class IxTraverser{

    private IxPath i_appendHead;
    private IxPath i_appendTail;

    QCandidates i_candidates;

    private IxPath i_greatHead;
    private IxPath i_greatTail;

    YapDataType i_handler;

    private IxPath i_smallHead;
    private IxPath i_smallTail;
    Tree i_tree;

    boolean[] i_take;

    private void add(IxPath a_previousPath, IxPath a_great, IxPath a_small) {
        addPathTree(a_previousPath);
        if (a_great != null && a_small != null && a_great.carriesTheSame(a_small)) {
            add(a_great, a_great.i_next, a_small.i_next);
            return;
        }
        addGreater(a_small);
        addSmaller(a_great);
    }

    private void addGreater(IxPath a_path) {
        if (a_path != null) {
            if (a_path.i_next == null) {
                addSubsequent(a_path);
            } else {
                if (a_path.i_next.i_tree == a_path.i_tree.i_preceding) {
                    addSubsequent(a_path);
                } else {
                    addPathTree(a_path);
                }
                addGreater(a_path.i_next);
            }
        }
    }

    private void addPathTree(IxPath a_path) {
        if (a_path != null) {
            i_tree = a_path.addToCandidatesTree(i_tree, i_candidates);
        }
    }
    
    private void addAll(Tree a_tree){
        if(a_tree != null){
            i_tree = ((IxTree)a_tree).addToCandidatesTree(i_tree, i_candidates, null);
            addAll(a_tree.i_preceding);
            addAll(a_tree.i_subsequent);
        }
    }

    private void addPreceding(IxPath a_path) {
        addPathTree(a_path);
        addAll(a_path.i_tree.i_preceding);
    }

    private void addSmaller(IxPath a_path) {
        if (a_path != null) {
            if (a_path.i_next == null) {
                addPreceding(a_path);
            } else {
                if (a_path.i_next.i_tree == a_path.i_tree.i_subsequent) {
                    addPreceding(a_path);
                } else {
                    addPathTree(a_path);
                }
                addSmaller(a_path.i_next);
            }
        }
    }

    private void addSubsequent(IxPath a_path) {
        addPathTree(a_path);
        addAll(a_path.i_tree.i_subsequent);
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

    private void delayedAppend(IxTree a_tree, int a_comparisonResult) {
        if (i_appendHead == null) {
            i_appendHead = new IxPath(this, null, a_tree, a_comparisonResult);
            i_appendTail = i_appendHead;
        } else {
            i_appendTail = i_appendTail.append(a_tree, a_comparisonResult);
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
            i_greatTail = i_greatTail.append(a_tree, res);
            i_smallTail = i_smallTail.append(a_tree, res);
            findBoth();
        }
    }
    
    int findBoundsQuery(QConObject a_qcon, IxTree a_tree) {
        if (!a_qcon.i_evaluator.supportsIndex()) {
            return -1;
        }
        i_take = new boolean[] { false, false, false, false};
        a_qcon.i_evaluator.indexBitMap(i_take);
        return findBounds1(a_qcon.i_object, a_tree);
    }
    
    int findBoundsExactMatch(Object a_constraint, IxTree a_tree){
        i_take = new boolean[] { false, true, false, false};
        return findBounds1(a_constraint, a_tree);
    }

    private int findBounds1(Object a_constraint, IxTree a_tree) {

        if (a_tree != null) {

            i_handler = a_tree.handler();
            i_handler.prepareComparison(a_constraint);

            // TODO: Only use small or big path where necessary.

            int res = a_tree.compare(null);

            i_greatHead = new IxPath(this, null, a_tree, res);
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

    private void findGreatestEqual(IxTree a_tree) {
        int res = a_tree.compare(null);
        i_greatTail = i_greatTail.append(a_tree, res);
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
            delayedAppend(a_tree, res);
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
        i_smallTail = i_smallTail.append(a_tree, res);
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
            delayedAppend(a_tree, res);
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

    Tree getMatches(QCandidates a_candidates) {
        i_candidates = a_candidates;
        i_tree = null;
        if (i_take[1]) {
            if (i_greatHead != null) {
                add(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
            }
        }
        if (i_take[0]) {
            IxPath head = i_smallHead;
            while (head != null) {
                i_tree = head.addPrecedingToCandidatesTree(i_tree, a_candidates);
                head = head.i_next;
            }
        }
        if (i_take[2]) {
            IxPath head = i_greatHead;
            while (head != null) {
                i_tree = head.addSubsequentToCandidatesTree(i_tree, a_candidates);
                head = head.i_next;
            }
        }
        return i_tree;
    }

    private void resetDelayedAppend() {
        i_appendHead = null;
        i_appendTail = null;
    }

}
