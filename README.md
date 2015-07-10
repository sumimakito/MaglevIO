# MaglevIO

[![Build Status](https://travis-ci.org/SumiMakito/MaglevIO.svg?branch=master)](https://travis-ci.org/SumiMakito/MaglevIO)

An easy-to-use and efficient Java I/O library. Based on Java NIO.

一个易用且高效的Java I/O操作库，构建于NIO基础之上。

Why to use NIO? 为什么选用NIO？
---
- Non-blocking IO 非阻塞式IO
- Channels and Buffers 借助通道与缓冲区对数据进行操作
- Selectors 可处理多通道的选择器

Benchmark 性能测试
---
数据准备中，近期更新。

Tutorial 使用指南
---
STEP 0: Add MaglevIO to your project 将MaglevIO添加至你的项目
- Download latest [JAR](https://github.com/SumiMakito/MaglevIO/releases)
- Add dependencies using ```Gradle``` or ```Maven``` will support soon.

STEP 1: Import 引入

```java
import com.github.sumimakito.maglevio.*;
```

STEP 2: Enjoy(Use) 享受(使用)

- Read a file 读取一个文件(花式文件读取大赛)
```java
try {
  //Using IO
  byte[] bytes1 = MaglevReader.IO.fileToBytes("/home/xcode.dmg");
  String string1 = MaglevReader.IO.fileToString("/home/xcode.dmg");
  //Using NIO-BFR(Highspeed)
  byte[] bytes2 = MaglevReader.NIO.BFR.fileToBytes("/home/xcode.dmg");
  String string2 = MaglevReader.NIO.BFR.fileToString("/home/xcode.dmg");
  //Using NIO-MappedBFR(Highspeed)
  byte[] bytes3 = MaglevReader.NIO.MappedBFR.fileToBytes("/home/xcode.dmg");
  String string3 = MaglevReader.NIO.MappedBFR.fileToString("/home/xcode.dmg");
} catch (IOException e) {
  //You need to catch the exception
  e.printStackTrace();
}
```

- Process a stream 处理流(处理一条小溪)
```java
try {
  //Read InputStream to an array
  byte[] bytes4 = MaglevReader.NIO.BFR.inputStreamToBytes(inputStream);
  //You need to pass a Charset object in order to get a correct decoded String
  String string4 = MaglevReader.NIO.BFR.inputStreamToString(inputStream, StandardCharsets.UTF_8);
  //Copy InputStream to OutputStream using NIO
  MaglevReader.NIO.BFR.copyInputToOutput(inputStream, outputStream);
} catch (IOException e) {
  //You need to catch the exception
  e.printStackTrace();
}
```

- Write to file 写入文件
```java
try {
  byte[] bytes = "String to write".getBytes();
  //byte[] to file
  MaglevWriter.NIO.BFR.writeBytesToFile(bytes, "/home/xcode.dmg");
  MaglevWriter.NIO.MappedBFR.writeBytesToFile(bytes, "/home/xcode.dmg");
  //String to file
  MaglevWriter.NIO.BFR.writeStringToFile("String to write", "/home/xcode.dmg");
  MaglevWriter.NIO.MappedBFR.writeStringToFile("String to write", "/home/xcode.dmg");
  //String with specific Charset to file
  MaglevWriter.NIO.BFR.writeStringToFileWithCharset("String to write", StandardCharsets.UTF_8, "/home/xcode.dmg");
  MaglevWriter.NIO.MappedBFR.writeStringToFileWithCharset("String to write", StandardCharsets.UTF_8, "/home/xcode.dmg");
} catch (Exception e) {
  //You need to catch the exception
  e.printStackTrace();
}
```

版权信息
---
```
Copyright 2014-2015 Sumi Makito & REINA Developing Group

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
