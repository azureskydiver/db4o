/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 *  
 */
class IxFileRangeReader implements Readable {

    private int               i_address;
    private int               i_baseAddress;

    IxFileRange               i_fileRange;
    private final YapDataType i_handler;
    private QCandidates       i_candidates;

    private int               i_lower;
    private int               i_upper;
    private int               i_cursor;

    private final YapReader   i_reader;
    private final int         i_slotLength;
    
    private final int         i_linkLegth;

    IxFileRangeReader(YapDataType a_handler) {
        i_handler = a_handler;
        i_linkLegth = a_handler.linkLength();
        i_slotLength = i_linkLegth + YapConst.YAPINT_LENGTH;
        i_reader = new YapReader(i_slotLength);
    }

    Tree add(IxFileRange a_fr, final Tree a_new) {
        setFileRange(a_fr);
        YapFile yf = a_fr.stream();
        Transaction trans = a_fr.trans();
        while (true) {
            i_reader.read(yf, i_address);
            i_reader.i_offset = 0;
            int cmp = i_handler.compareTo(i_handler.indexObject(trans,
                i_handler.readIndexEntry(i_reader)));

            if (cmp == 0) {
                int parentID = i_reader.readInt();
                cmp = parentID - ((IxPatch) a_new).i_parentID;
            }
            if (cmp > 0) {
                i_upper = i_cursor - 1;
                if (i_upper < i_lower) {
                    i_upper = i_lower;
                }
            } else if (cmp < 0) {
                i_lower = i_cursor + 1;
                if (i_lower > i_upper) {
                    i_lower = i_upper;
                }
            } else {

                if (a_new instanceof IxRemove) {
                    IxRemove ir = (IxRemove) a_new;
                    if (i_cursor == 0) {
                        a_new.i_preceding = a_fr.i_preceding;
                        if (a_fr.i_entries == 1) {
                            a_new.i_subsequent = a_fr.i_subsequent;
                            return a_new.balanceCheckNulls();
                        }
                        a_fr.i_entries--;
                        a_fr.i_address += i_slotLength;
                        a_fr.i_preceding = null;
                        a_new.i_subsequent = a_fr;
                    } else if (i_cursor + 1 == a_fr.i_entries) {
                        a_new.i_preceding = a_fr;
                        a_new.i_subsequent = a_fr.i_subsequent;
                        a_fr.i_subsequent = null;
                        a_fr.i_entries--;
                    } else {
                        return insert(a_new, i_cursor, 0);
                    }
                    a_fr.calculateSize();
                    return a_new.balanceCheckNulls();
                } else {
                    if (i_cursor == 0) {
                        a_new.i_subsequent = a_fr;
                        return a_new.rotateLeft();
                    } else if (i_cursor == a_fr.i_entries) {
                        a_new.i_preceding = a_fr;
                        return a_new.rotateRight();
                    }
                    return insert(a_new, i_cursor, cmp);
                }
            }
            if (!adjustCursor()) {
                if (i_cursor == 0 && cmp > 0) {
                    return a_fr.add(a_new, 1);
                }
                if (i_cursor == a_fr.i_entries - 1 && cmp < 0) {
                    return a_fr.add(a_new, -1);
                }
                return insert(a_new, i_cursor, cmp);
            }
        }
    }

    public Tree addToCandidatesTree(QCandidates a_candidates, Tree a_tree,
        IxFileRange a_range, int[] a_LowerAndUpperMatch) {

        i_candidates = a_candidates;

        if (a_LowerAndUpperMatch == null) {
            a_LowerAndUpperMatch = new int[] { 0, a_range.i_entries - 1};
        }

        YapFile yf = i_fileRange.stream();

        int baseAddress = a_range.i_address;

        final boolean sorted = false;

        if (sorted) {
            
            // The sorted implementation was identified as a bottleneck.
            // Keep it, in case a sorted QCandidate tree turns out to be necessary.

            int offset = i_handler.linkLength();
            for (int i = a_LowerAndUpperMatch[0]; i <= a_LowerAndUpperMatch[1]; i++) {
                int address = baseAddress + (i * i_slotLength);
                i_reader.read(yf, address);
                i_reader.i_offset = offset;
                QCandidate candidate = new QCandidate(a_candidates, i_reader
                    .readInt(), true);
                a_tree = Tree.add(a_tree, candidate);
            }

        } else {
            int count = a_LowerAndUpperMatch[1] - a_LowerAndUpperMatch[0] + 1;
            if(count > 0) {
	            YapReader reader = new YapReader(count * i_slotLength);
	            reader.read(yf, baseAddress + a_LowerAndUpperMatch[0] * i_slotLength);
	            Tree tree = new TreeReader(reader, this, false).read(count);
	            if(tree != null) {
	                a_tree = Tree.add(a_tree, tree);
	            }
            }
        }
        
        return a_tree;
    }

    private boolean adjustCursor() {
        if(i_upper < i_lower) {
            return false;
        }
        int oldCursor = i_cursor;
        i_cursor = i_lower + ((i_upper - i_lower) / 2);
        if (i_cursor == oldCursor && i_cursor == i_lower && i_lower < i_upper) {
            i_cursor++;
        }
        i_address = i_baseAddress + (i_cursor * i_slotLength);
        return i_cursor != oldCursor;
    }

    int compare(IxFileRange a_fr, Tree a_to) {
        setFileRange(a_fr);
        YapFile yf = a_fr.stream();
        Transaction trans = a_fr.trans();
        while (true) {
            i_reader.read(yf, i_address);
            i_reader.i_offset = 0;
            
            int cmp = i_handler.compareTo(i_handler.indexObject(trans,
                i_handler.readIndexEntry(i_reader)));
            
            if (cmp > 0) {
                i_upper = i_cursor - 1;
            } else if (cmp < 0) {
                i_lower = i_cursor + 1;
            } else {
                return 0;
            }
            if (!adjustCursor()) {

                // TODO: What happens, if we have an inside hit here?

                return i_cursor == 0 ? 1 : -1;
            }
        }
    }

    private Tree insert(Tree a_new, int a_cursor, int a_cmp) {
        int incStartNewAt = a_cmp <= 0 ? 1 : 0;
        int newBaseAddress = i_baseAddress
            + ((a_cursor + incStartNewAt) * i_slotLength);
        int newEntries = i_fileRange.i_entries - a_cursor - incStartNewAt;
        i_fileRange.i_entries = a_cmp < 0 ? a_cursor + 1 : a_cursor;
        
        IxFileRange ifr = new IxFileRange(i_fileRange.i_fieldTransaction,
            newBaseAddress, newEntries);
        
        ifr.i_subsequent = i_fileRange.i_subsequent;
        i_fileRange.i_subsequent = null;
        a_new.i_preceding = i_fileRange.balanceCheckNulls();
        a_new.i_subsequent = ifr.balanceCheckNulls();
        return a_new.balance();
    }

    int[] lowerAndUpperMatches() {
        int[] matches = new int[] { i_lower, i_upper};
        if(i_lower > i_upper) {
            return matches;
        }
        YapFile yf = i_fileRange.stream();
        Transaction trans = i_fileRange.trans();
        int tempCursor = i_cursor;
        i_upper = i_cursor;
        adjustCursor();
        while (true) {
            i_reader.read(yf, i_address);
            i_reader.i_offset = 0;
            int cmp = i_handler.compareTo(i_handler.indexObject(trans,
                i_handler.readIndexEntry(i_reader)));
            if (cmp == 0) {
                i_upper = i_cursor;
            } else {
                i_lower = i_cursor + 1;
                if (i_lower > i_upper) {
                    matches[0] = i_upper;
                    break;
                }
            }
            if (!adjustCursor()) {
                matches[0] = i_upper;
                break;
            }
        }
        i_upper = matches[1];
        i_lower = tempCursor;
        if(i_lower > i_upper) {
            i_lower = i_upper;
        }
        adjustCursor();
        while (true) {
            i_reader.read(yf, i_address);
            i_reader.i_offset = 0;
            int cmp = i_handler.compareTo(i_handler.indexObject(trans,
                i_handler.readIndexEntry(i_reader)));
            if (cmp == 0) {
                i_lower = i_cursor;
            } else {
                i_upper = i_cursor - 1;
                if (i_upper < i_lower) {
                    matches[1] = i_lower;
                    break;
                }
            }
            if (!adjustCursor()) {
                matches[1] = i_lower;
                break;
            }
        }
        return matches;
    }

    private void setFileRange(IxFileRange a_fr) {
        i_fileRange = a_fr;
        i_lower = 0;
        i_upper = a_fr.i_entries - 1;
        i_baseAddress = a_fr.i_address;
        adjustCursor();
    }

    public Object read(YapReader a_reader) {
        a_reader.incrementOffset(i_linkLegth);
        return new QCandidate(i_candidates, a_reader.readInt(), true);
    }

    public int byteCount() {
        return i_slotLength;
    }
}