gdal-scala
==========

This is a scala wrapper for the GDAL java bindings. To use it, you must have built GDAL along with the java bindings, have all of the needed dynamic library files in /usr/local/lib (including the java binding files), and copy gdal.jar into the `gdal/lib` subdirectory.

This library aims specifically to wrap read operaitons of rasters; writing rasters is not implemented.

Building\Installing GDAL java bindings
======================================


MAC OSX
-------

java.opts needs to be set to something like:

```
JAVA_HOME = /System/Library/Frameworks/JavaVM.framework/Versions/Current
JAVADOC=$(JAVA_HOME)/Commands/javadoc
JAVAC=$(JAVA_HOME)/Commands/javac
JAVA=$(JAVA_HOME)/Commands/java
JAR=$(JAVA_HOME)/Commands/jar
JAVA_INCLUDE=-I$(JAVA_HOME)/Headers
```

```
> brew install proj
> # get gdal source
> cd gdal/gdal
> ./configure
> make install
> cd swig/java
> make
> cp .libs/*.dylib /usr/local/lib
```

`fork in run := true` and `fork in test := true` must be set in order for the `javaOptions += "-Djava.library.path=/usr/local/lib"`
