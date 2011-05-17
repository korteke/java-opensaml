/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** File helper functions. */
public final class FileSupport {

    /** Constructor. */
    private FileSupport() {

    }

    /**
     * Reads the contents of a file in to a byte array.
     * 
     * @param file file to read
     * @return the byte contents of the file
     * 
     * @throws IOException throw if there is a problem reading the file in to the byte array
     */
    public static byte[] fileToByteArray(final File file) throws IOException {
        Assert.isNotNull(file, "File may not be null");
        Assert.isTrue(file.exists(), "File does not exist");
        Assert.isTrue(file.canRead(), "File is not readable");

        final long numOfBytes = file.length();

        if (numOfBytes > Integer.MAX_VALUE) {
            throw new IOException("File is to large to be read in to a byte array");
        }

        final byte[] bytes = new byte[(int) numOfBytes];
        final FileInputStream ins = new FileInputStream(file);
        int offset = 0;
        int numRead = 0;
        do {
            numRead = ins.read(bytes, offset, bytes.length - offset);
            offset += numRead;
        } while (offset < bytes.length && numRead >= 0);

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        CloseableSupport.closeQuietly(ins);

        return bytes;
    }
    
    /**
     * Copy a source file to a target file.  If the target file exists, it will be first deleted.
     * 
     * @param source the source File
     * @param target the target File
     * @throws IOException 
     */
    public static void copyFile(final File source, final File target) throws IOException {
        if (target.exists()) {
            target.delete();
        }
        
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
}