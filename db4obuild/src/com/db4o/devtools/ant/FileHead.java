package com.db4o.devtools.ant;

import java.io.*;

public class FileHead extends File {
    
    private final FileHeadAntTask task;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 4) {
            String errMsg = "Usage: FileHead#main(path, header, before, fileExt)";
            System.err.println(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        FileHeadAntTask task = new FileHeadAntTask();
        task.setPath(args[0]);
        task.setHeader(args[1]);
        task.setBefore(args[2]);
        task.setFileExt(args[3]);
        new FileHead(args[0], task).run();
    }

    public FileHead(String file, FileHeadAntTask task) {
        super(file);
        this.task = task;
    }

    public FileHead(String dir, String file, FileHeadAntTask task) {
        super(dir, file);
        this.task = task;
    }

    public void run() throws Exception {
        if (isDirectory()) {
            String[] files = list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    FileHead child = new FileHead(getAbsolutePath(),
                        files[i], task);
                    if (child.isDirectory() || task.fileExt == null
                        || files[i].endsWith(task.fileExt)) {
                        child.run();
                    }
                }
            }
        } else {
            String path = getAbsolutePath();
            final int bufferSize = 64000;
            RandomAccessFile rafIn = new RandomAccessFile(path, "r");
            RandomAccessFile rafOut = new RandomAccessFile(path + "g", "rw");
            rafOut.write(task.header);
            int filepos = 0;
            int beforePos = 0;
            while (beforePos < task.before.length) {
                byte b = (byte) rafIn.read();
                filepos++;
                if (b == task.before[beforePos]) {
                    beforePos++;
                } else {
                    beforePos = 0;
                }
            }
            filepos -= beforePos;
            rafIn.seek(filepos);

            long len = rafIn.length() - filepos;
            byte[] bytes = new byte[bufferSize];
            while (len > 0) {
                len -= bufferSize;
                if (len < 0) {
                    bytes = new byte[(int) (len + bufferSize)];
                }
                rafIn.read(bytes);
                rafOut.write(bytes);
            }

            rafIn.close();
            rafOut.close();
            new File(path).delete();
            new File(path + "g").renameTo(new File(path));
        }
    }
}