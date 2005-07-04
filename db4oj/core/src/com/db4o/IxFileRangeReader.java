/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 *  
 */
class IxFileRangeReader {

    private int               _baseAddress;
    private int               _baseAddressOffset;
    private int               _addressOffset;

    private final YapDataType _handler;
    
    // FIXME: _fileRange should not be stored here
    private IxFileRange       _fileRange;

    
    // TODO: IxFileRangeReader should not store result internally
    // Instead send it back to the caller in a small object and
    // pass it again in case of later use.
    // After this change IxField#fileRangeReader can create a new
    // one every time and we do not have to worry about cleaning
    // up _candidates variable after use.
    private int               _lower;
    private int               _upper;
    private int               _cursor;

    private final YapReader   _reader;
    private final int         _slotLength;

    private final int         _linkLegth;

    IxFileRangeReader(YapDataType handler) {
        _handler = handler;
        _linkLegth = handler.linkLength();
        _slotLength = _linkLegth + YapConst.YAPINT_LENGTH;
        _reader = new YapReader(_slotLength);
    }

    Tree add(IxFileRange fileRange, final Tree newTree) {
        setFileRange(fileRange);
        YapFile yf = fileRange.stream();
        Transaction trans = fileRange.trans();
        while (true) {
            _reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;
            int cmp = _handler.compareTo(_handler.comparableObject(trans, _handler
                .readIndexEntry(_reader)));
            if (cmp == 0) {
                int parentID = _reader.readInt();
                cmp = parentID - ((IxPatch) newTree).i_parentID;
            }
            if (cmp > 0) {
                _upper = _cursor - 1;
                if (_upper < _lower) {
                    _upper = _lower;
                }
            } else if (cmp < 0) {
                _lower = _cursor + 1;
                if (_lower > _upper) {
                    _lower = _upper;
                }
            } else {
                if (newTree instanceof IxRemove) {
                    IxRemove ir = (IxRemove) newTree;
                    if (_cursor == 0) {
                        newTree.i_preceding = fileRange.i_preceding;
                        if (fileRange._entries == 1) {
                            newTree.i_subsequent = fileRange.i_subsequent;
                            return newTree.balanceCheckNulls();
                        }
                        fileRange._entries--;
                        fileRange.incrementAddress(_slotLength);
                        fileRange.i_preceding = null;
                        newTree.i_subsequent = fileRange;
                    } else if (_cursor + 1 == fileRange._entries) {
                        newTree.i_preceding = fileRange;
                        newTree.i_subsequent = fileRange.i_subsequent;
                        fileRange.i_subsequent = null;
                        fileRange._entries--;
                    } else {
                        return insert(fileRange, newTree, _cursor, 0);
                    }
                    fileRange.calculateSize();
                    return newTree.balanceCheckNulls();
                } else {
                    if (_cursor == 0) {
                        newTree.i_subsequent = fileRange;
                        return newTree.rotateLeft();
                    } else if (_cursor == fileRange._entries) {
                        newTree.i_preceding = fileRange;
                        return newTree.rotateRight();
                    }
                    return insert(fileRange, newTree, _cursor, cmp);
                }
            }
            if (!adjustCursor()) {
                if (_cursor == 0 && cmp > 0) {
                    return fileRange.add(newTree, 1);
                }
                if (_cursor == fileRange._entries - 1 && cmp < 0) {
                    return fileRange.add(newTree, -1);
                }
                return insert(fileRange, newTree, _cursor, cmp);
            }
        }
    }
    
    public void visit(Visitor4 visitor, IxFileRange fileRange, int[] lowerAndUpperMatch) {
        if (lowerAndUpperMatch == null) {
            lowerAndUpperMatch = new int[] { 0, fileRange._entries - 1};
        }
        int count = lowerAndUpperMatch[1] - lowerAndUpperMatch[0] + 1;
        if (count > 0) {
            YapReader reader = new YapReader(count * _slotLength);
            reader.read(fileRange.stream(), fileRange._address, fileRange._addressOffset
                + (lowerAndUpperMatch[0] * _slotLength));

            for (int i = lowerAndUpperMatch[0]; i <= lowerAndUpperMatch[1]; i++) {
                reader.incrementOffset(_linkLegth);
                visitor.visit(new Integer(reader.readInt()));
            }
        }
    }

    private boolean adjustCursor() {
        if (_upper < _lower) {
            return false;
        }
        int oldCursor = _cursor;
        _cursor = _lower + ((_upper - _lower) / 2);
        if (_cursor == oldCursor && _cursor == _lower && _lower < _upper) {
            _cursor++;
        }
        _addressOffset = _cursor * _slotLength;
        return _cursor != oldCursor;
    }

    int compare(IxFileRange fileRange, Tree treeTo) {
        setFileRange(fileRange);
        YapFile yf = fileRange.stream();
        Transaction trans = fileRange.trans();
        while (true) {
            _reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;

            int cmp = _handler.compareTo(_handler.comparableObject(trans, _handler
                .readIndexEntry(_reader)));

            if (cmp > 0) {
                _upper = _cursor - 1;
            } else if (cmp < 0) {
                _lower = _cursor + 1;
            } else {
                return 0;
            }
            if (!adjustCursor()) {
                return _cursor == 0 ? cmp : -1;
            }
        }
    }

    private Tree insert(IxFileRange fileRange, Tree a_new, int a_cursor, int a_cmp) {
        int incStartNewAt = a_cmp <= 0 ? 1 : 0;
        int newAddressOffset = (a_cursor + incStartNewAt) * _slotLength;
        int newEntries = fileRange._entries - a_cursor - incStartNewAt;
        if(Deploy.debug){
	        if(newEntries == 0){
	            // A bug in P1Object made this happen.
	            // It looke like it occurs if (a_cmp == 0)
	            // We may have to deal with this again, if we get similar
	            // entries on the same object (indexing arrays), 
	            // so (a_cmp == 0)
	            throw new RuntimeException("No zero new entries permitted here.");
	        }
        }
        
        fileRange._entries = a_cmp < 0 ? a_cursor + 1 : a_cursor;
        IxFileRange ifr = new IxFileRange(fileRange.i_fieldTransaction,
            _baseAddress, _baseAddressOffset + newAddressOffset, newEntries);
        ifr.i_subsequent = fileRange.i_subsequent;
        fileRange.i_subsequent = null;
        a_new.i_preceding = fileRange.balanceCheckNulls();
        a_new.i_subsequent = ifr.balanceCheckNulls();
        return a_new.balance();
    }

    int[] lowerAndUpperMatches() {
        int[] matches = new int[] { _lower, _upper};
        if (_lower > _upper) {
            return matches;
        }
        YapFile yf = _fileRange.stream();
        Transaction trans = _fileRange.trans();
        int tempCursor = _cursor;
        _upper = _cursor;
        adjustCursor();
        while (true) {
            _reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;
            int cmp = _handler.compareTo(_handler.comparableObject(trans, _handler
                .readIndexEntry(_reader)));
            if (cmp == 0) {
                _upper = _cursor;
            } else {
                _lower = _cursor + 1;
                if (_lower > _upper) {
                    matches[0] = _upper;
                    break;
                }
            }
            if (!adjustCursor()) {
                matches[0] = _upper;
                break;
            }
        }
        _upper = matches[1];
        _lower = tempCursor;
        if (_lower > _upper) {
            _lower = _upper;
        }
        adjustCursor();
        while (true) {
            _reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;
            int cmp = _handler.compareTo(_handler.comparableObject(trans, _handler
                .readIndexEntry(_reader)));
            if (cmp == 0) {
                _lower = _cursor;
            } else {
                _upper = _cursor - 1;
                if (_upper < _lower) {
                    matches[1] = _lower;
                    break;
                }
            }
            if (!adjustCursor()) {
                matches[1] = _lower;
                break;
            }
        }
        return matches;
    }

    private void setFileRange(IxFileRange a_fr) {
        _fileRange = a_fr;
        _lower = 0;
        _upper = a_fr._entries - 1;
        _baseAddress = a_fr._address;
        _baseAddressOffset = a_fr._addressOffset;
        adjustCursor();
    }

    public int byteCount() {
        return _slotLength;
    }
}