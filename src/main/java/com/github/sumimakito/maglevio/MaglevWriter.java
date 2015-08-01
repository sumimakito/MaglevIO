/*
 * Copyright 2014-2015 Sumi Makito & REINA Developing Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sumimakito.maglevio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MaglevWriter {
    public static class NIO {
        public static class BFR {
            public static void writeBytesToFile(final byte[] bytes, final String pFilePath) throws Exception {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(pFilePath);
                    FileChannel fcOut = out.getChannel();
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    fcOut.write(buffer);
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            throw e;
                        }
                    }
                }
            }

            public static void writeStringToFile(final String string, final String pFilePath) throws Exception {
                writeBytesToFile(string.getBytes(), pFilePath);
            }

            public static void writeStringToFileWithCharset(final String string, final Charset charset, final String pFilePath) throws Exception {
                writeBytesToFile(string.getBytes(charset.name()), pFilePath);
            }
        }

        public static class MappedBFR {
            public static void writeBytesToFile(final byte[] bytes, final String pFilePath, boolean isAppend) throws IOException {
                final File file = new File(pFilePath);
                if(!isAppend){
                    file.delete();
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(raf.length());
                FileChannel fc = raf.getChannel();
                MappedByteBuffer mbf = fc.map(FileChannel.MapMode.READ_WRITE, fc.position(), bytes.length);
                fc.close();
                mbf.put(bytes);
            }

            public static void writeBytesToFile(final byte[] bytes, final String pFilePath) throws IOException {
                writeBytesToFile(bytes, pFilePath, false);
            }

            public static void writeStringToFile(final String string, final String pFilePath) throws IOException {
                writeBytesToFile(string.getBytes(), pFilePath, false);
            }

            public static void writeStringToFile(final String string, final String pFilePath, boolean isAppend) throws IOException {
                writeBytesToFile(string.getBytes(), pFilePath, isAppend);
            }

            public static void writeStringToFileWithCharset(final String string, final Charset charset, final String pFilePath, boolean isAppend) throws IOException {
                writeBytesToFile(string.getBytes(charset.name()), pFilePath, isAppend);
            }
        }
    }
}
