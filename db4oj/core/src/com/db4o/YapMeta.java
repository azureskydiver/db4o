/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public abstract class YapMeta {
    
    int i_id = 0; // UID and address of pointer to the object in our file

    protected int i_state = 2; // DIRTY and ACTIVE

    final boolean beginProcessing() {
        if (bitIsTrue(YapConst.PROCESSING)) {
            return false;
        }
        bitTrue(YapConst.PROCESSING);
        return true;
    }

    final void bitFalse(int bitPos) {
        i_state &= ~(1 << bitPos);
    }
    
    final boolean bitIsFalse(int bitPos) {
        return (i_state | (1 << bitPos)) != i_state;
    }

    final boolean bitIsTrue(int bitPos) {
        return (i_state | (1 << bitPos)) == i_state;
    }

    final void bitTrue(int bitPos) {
        i_state |= (1 << bitPos);
    }

    void cacheDirty(Collection4 col) {
        if (!bitIsTrue(YapConst.CACHED_DIRTY)) {
            bitTrue(YapConst.CACHED_DIRTY);
            col.add(this);
        }
    }

    void endProcessing() {
        bitFalse(YapConst.PROCESSING);
    }

    public int getID() {
        return i_id;
    }

    abstract byte getIdentifier();

    public final boolean isActive() {
        return bitIsTrue(YapConst.ACTIVE);
    }

    public boolean isDirty() {
        return bitIsTrue(YapConst.ACTIVE) && (!bitIsTrue(YapConst.CLEAN));
    }

    public int linkLength() {
        return YapConst.YAPID_LENGTH;
    }

    final void notCachedDirty() {
        bitFalse(YapConst.CACHED_DIRTY);
    }

    abstract int ownLength();

    void read(Transaction a_trans) {
        try {
            if (beginProcessing()) {
                YapReader reader = a_trans.i_stream.readReaderByID(a_trans, getID());
                if (reader != null) {
                    if (Deploy.debug) {
                        reader.readBegin(getID(), getIdentifier());
                    }
                    readThis(a_trans, reader);
                    setStateOnRead(reader);
                }
                endProcessing();
            }
        } catch (LongJumpOutException ljoe) {
            throw ljoe;
        } catch (Throwable t) {
            if (Debug.atHome) {
                t.printStackTrace();
            }
        }
    }
    
    abstract void readThis(Transaction a_trans, YapReader a_reader);


    void setID(YapStream a_stream, int a_id) {
        i_id = a_id;
    }

    final void setStateClean() {
        bitTrue(YapConst.ACTIVE);
        bitTrue(YapConst.CLEAN);
    }

    final void setStateDeactivated() {
        bitFalse(YapConst.ACTIVE);
    }

    void setStateDirty() {
        bitTrue(YapConst.ACTIVE);
        bitFalse(YapConst.CLEAN);
    }

    void setStateOnRead(YapReader reader) {
        if (Deploy.debug) {
            reader.readEnd();
        }
        if (bitIsTrue(YapConst.CACHED_DIRTY)) {
            setStateDirty();
        } else {
            setStateClean();
        }
    }

    YapWriter write(YapStream a_stream, Transaction a_trans) {
        if (writeObjectBegin()) {

            YapWriter writer =
                (getID() == 0)
                    ? a_stream.newObject(a_trans, this)
                    : a_stream.updateObject(a_trans, this);

            writeThis(writer);

            if (Deploy.debug) {
                writer.writeEnd();
                writer.debugCheckBytes();
            }

            ((YapFile)a_stream).writeObject(this, writer);

            if (isActive()) {
                setStateClean();
            }
            endProcessing();

            if (Debug.verbose) {
                if (a_stream instanceof YapClient) {
                    System.out.println(
                        "YapMeta:write(): " + this.getClass().getName() + " " + getID());
                }
            }

            return writer;
        }
        return null;
    }

    boolean writeObjectBegin() {
        if (isDirty()) {
            return beginProcessing();
        }
        return false;
    }

    void writeOwnID(YapWriter a_writer) {
        write(a_writer.getStream(), a_writer.getTransaction());
        a_writer.writeInt(getID());
    }

    abstract void writeThis(YapWriter a_writer);

    static final void writeIDOf(YapMeta a_object, YapWriter a_writer) {
        if (a_object != null) {
            a_object.writeOwnID(a_writer);
        } else {
            a_writer.writeInt(0);
        }
    }
}
