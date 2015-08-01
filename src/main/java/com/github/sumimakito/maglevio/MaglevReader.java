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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

public class MaglevReader {

    public static class IO {
        public static String fileToString(final String pFilePath) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pFilePath));
            StringBuilder sb = new StringBuilder();
            String rLine = null;
            while ((rLine = bufferedReader.readLine()) != null) {
                sb.append(rLine);
            }
            bufferedReader.close();
            return sb.toString();
        }

        public static byte[] fileToBytes(final String pFilePath) throws IOException {
            File file = new File(pFilePath);
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            fileInputStream.close();
            return bytes;
        }
    }

    public static class NIO {
        public static class BFR {
            public static String fileToString(final String pFilePath) throws IOException {
                RandomAccessFile file = new RandomAccessFile(pFilePath, "r");
                FileChannel fileChannel = file.getChannel();
                ByteBuffer buffer = ByteBuffer.allocateDirect((int) fileChannel.size());
                fileChannel.read(buffer);
                buffer.flip();
                CharBuffer charBuffer = Charset.defaultCharset().decode(buffer);
                BufferedReader bufferedReader = new BufferedReader(new StringReader(charBuffer.toString()));
                StringBuilder sb = new StringBuilder();
                String rLine = null;
                while ((rLine = bufferedReader.readLine()) != null) {
                    sb.append(rLine);
                }
                bufferedReader.close();
                file.close();
                return sb.toString();
            }

            public static byte[] fileToBytes(final String pFilePath) throws IOException {
                RandomAccessFile file = new RandomAccessFile(pFilePath, "r");
                FileChannel fileChannel = file.getChannel();
                ByteBuffer buffer = ByteBuffer.allocateDirect((int) fileChannel.size());
                fileChannel.read(buffer);
                buffer.flip();
                int size = (int) fileChannel.size();
                byte[] bytes = new byte[size];
                buffer.get(bytes);
                file.close();
                return bytes;
            }

            public static String inputStreamToString(final InputStream is,
                                                     final Charset charset) throws IOException {
                try {
                    StringBuilder out = new StringBuilder();
                    byte[] b = new byte[4096];
                    byte[] savedBytes = new byte[1];
                    boolean hasSavedBytes = false;
                    CharsetDecoder decoder = charset.newDecoder();
                    for (int n; (n = is.read(b)) != -1; ) {
                        if (hasSavedBytes) {
                            byte[] bTmp = new byte[savedBytes.length + b.length];
                            System.arraycopy(savedBytes, 0, bTmp, 0,
                                    savedBytes.length);
                            System.arraycopy(b, 0, bTmp, savedBytes.length, b.length);
                            b = bTmp;
                            hasSavedBytes = false;
                            n = n + savedBytes.length;
                        }

                        CharBuffer charBuffer = MaglevNIOUtil.decodeHelper(b, n, charset);
                        if (charBuffer == null) {
                            int nrOfChars = 0;
                            while (charBuffer == null) {
                                nrOfChars++;
                                charBuffer = MaglevNIOUtil.decodeHelper(b, n - nrOfChars, charset);
                                if (nrOfChars > 10 && nrOfChars < n) {
                                    try {
                                        charBuffer = decoder.decode(ByteBuffer.wrap(b, 0, n));
                                    } catch (MalformedInputException ex) {
                                        throw new IOException(
                                                "Encoding not matched! (" + charset.displayName() + ")", ex);
                                    }
                                }
                            }
                            savedBytes = new byte[nrOfChars];
                            hasSavedBytes = true;
                            for (int i = 0; i < nrOfChars; i++) {
                                savedBytes[i] = b[n - nrOfChars + i];
                            }
                        }

                        charBuffer.rewind(); //Set *ptr -> 0.
                        out.append(charBuffer.toString());
                    }
                    if (hasSavedBytes) {
                        try {
                            CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(savedBytes, 0, savedBytes.length));
                        } catch (MalformedInputException ex) {
                            throw new IOException("Encoding not matched! (" + charset.displayName() + ")", ex);
                        }
                    }
                    return out.toString();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }

            public static byte[] inputStreamToBytes(final InputStream is) throws IOException {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024 * 8];
                    int len = -1;
                    while ((len = is.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    return baos.toByteArray();
                } finally {
                    is.close();
                }
            }

            public static void copyInputToOutput(final InputStream is, final OutputStream os) throws IOException {
                final ReadableByteChannel inChannel = Channels.newChannel(is);
                final WritableByteChannel outChannel = Channels.newChannel(os);

                try {
                    final ByteBuffer buffer = ByteBuffer.allocate(65536);
                    while (true) {
                        int bytesRead = inChannel.read(buffer);
                        if (bytesRead == -1) break;
                        buffer.flip();
                        while (buffer.hasRemaining()) outChannel.write(buffer);
                        buffer.clear();
                    }
                } finally {
                    try {
                        inChannel.close();
                    } catch (IOException ex) {
                        System.out.println("Exception occurred while closing inChannel:\n" + ex);
                    }
                    try {
                        outChannel.close();
                    } catch (IOException ex) {
                        System.out.println("Exception occurred while closing outChannel:\n" + ex);
                    }
                }
            }
        }

        public static class MappedBFR {
            public static String fileToString(String pFilePath) throws IOException {
                int bufferSize = 1024;
                StringBuilder sb = new StringBuilder();
                RandomAccessFile fis = new RandomAccessFile(new File(pFilePath), "rw");
                FileChannel channel = fis.getChannel();
                long size = channel.size();
                bufferSize = MaglevNIOUtil.bufferSizeAllocator((int) size);
                MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
                byte[] bytes = new byte[bufferSize];
                int lrCount = (int) Math.ceil((double) size / (double) bufferSize);
                int rDelta = size % bufferSize != 0 ? (int) size % bufferSize : 0;
                for (int i = 0; i < lrCount - 1; i++) {
                    mappedByteBuffer.get(bytes, 0, bufferSize);
                    sb.append(new String(bytes));
                }
                if (rDelta > 0) {
                    bytes = new byte[rDelta];
                    mappedByteBuffer.get(bytes, 0, rDelta);
                    sb.append(new String(bytes));
                }
                channel.close();
                fis.close();
                return sb.toString();
            }

            public static byte[] fileToBytes(String pFilePath) throws IOException {
                RandomAccessFile raf = new RandomAccessFile(new File(pFilePath), "rw");
                FileChannel channel = raf.getChannel();
                long size = channel.size();
                int bufferSize = MaglevNIOUtil.bufferSizeAllocator((int) size);
                MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
                byte[] bytes = new byte[(int) size];

                int lrCount = (int) Math.ceil((double) size / (double) bufferSize);
                int rDelta = size % bufferSize != 0 ? (int) size % bufferSize : 0;
                for (int i = 0; i < lrCount - 1; i++) {
                    mappedByteBuffer.get(bytes, bufferSize * i, bufferSize);
                }
                if (rDelta > 0) {
                    mappedByteBuffer.get(bytes, bufferSize * (lrCount - 1), rDelta);
                }

                channel.close();
                raf.close();
                return bytes;
            }
        }
    }
}
