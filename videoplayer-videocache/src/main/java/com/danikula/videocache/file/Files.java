package com.danikula.videocache.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Utils for work with files.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class Files {

    private static final Logger LOG = LoggerFactory.getLogger("Files");

    static void makeDir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IOException("File " + directory + " is not directory!");
            }
        } else {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                throw new IOException(String.format("Directory %s can't be created", directory.getAbsolutePath()));
            }
        }
    }

    static List<File> getLruListFiles(File directory) {
        List<File> result = new LinkedList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            result = Arrays.asList(files);
            //由缓存文件最后一次修改时间排序
            Collections.sort(result, new LastModifiedComparator());
        }
        return result;
    }

    static void setLastModifiedNow(File file) throws IOException {
        if (file.exists()) {
            long now = System.currentTimeMillis();
            boolean modified = file.setLastModified(now); // on some devices (e.g. Nexus 5) doesn't work
            if (!modified) {
                modify(file);
                if (file.lastModified() < now) {
                    // NOTE: apparently this is a known issue (see: http://stackoverflow.com/questions/6633748/file-lastmodified-is-never-what-was-set-with-file-setlastmodified)
                    LOG.warn("Last modified date {} is not set for file {}", new Date(file.lastModified()), file.getAbsolutePath());
                }
            }
        }
    }
    /**
     * 修改文件
     * @param file
     * @throws IOException
     */
    static void modify(File file) throws IOException {
        long size = file.length();
        if (size == 0) {
            recreateZeroSizeFile(file);
            return;
        }

		RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
		accessFile.seek(size - 1);
		byte lastByte = accessFile.readByte();//读取  8-bit byte 
		accessFile.seek(size - 1);
		accessFile.write(lastByte);//写入上一字节数据
		accessFile.close();
	}

    private static void recreateZeroSizeFile(File file) throws IOException {
        if (!file.delete() || !file.createNewFile()) {
            throw new IOException("Error recreate zero-size file " + file);
        }
    }
    /**
     * Comparator
     */
    private static final class LastModifiedComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            return compareLong(lhs.lastModified(), rhs.lastModified());
        }

        private int compareLong(long first, long second) {
            return (first < second) ? -1 : ((first == second) ? 0 : 1);
        }
    }

}
