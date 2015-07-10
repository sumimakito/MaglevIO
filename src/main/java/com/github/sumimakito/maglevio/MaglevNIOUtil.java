/*
 * Copyright 2014-2015 Sumi Makito
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

public class MaglevNIOUtil {
    //An inner util class which should only be accessed by the local package.

    protected static MappedByteBuffer getBuffer(FileChannel fChannel, int size) throws IOException {
        return fChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
    }

    protected static int bufferSizeAllocator(int size) {
        //Support MappedBFR ONLY. Still improving...
        if (size < 1024 * 1024) {
            //4KB
            return 1024 * 4;
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 30) {
            //512KB
            return 1024 * 512;
        } else if (size >= 1024 * 1024 * 30 && size < 1024 * 1024 * 80) {
            //2MB
            return 1024 * 1024 * 2;
        } else if (size >= 1024 * 1024 * 80 && size < 1024 * 1024 * 500) {
            //4MB
            return 1024 * 1024 * 4;
        } else {
            //16MB
            return 1024 * 1024 * 16;
        }
    }

    protected static CharBuffer decodeHelper(byte[] byteArray, int numberOfBytes, java.nio.charset.Charset charset) throws IOException {
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = null;
        try {
            charBuffer = decoder.decode(ByteBuffer.wrap(byteArray, 0,
                    numberOfBytes));
        } catch (MalformedInputException ex) {
            charBuffer = null;
        }
        return charBuffer;
    }
}
