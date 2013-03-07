set JPCT_LIB=../../jpct/lib
set JPCT_JNI=-Djava.library.path=%JPCT_LIB%/lwjgl-2.8.0/native/windows
set JPCT_CP=%JPCT_LIB%/jpct/jpct.jar;%JPCT_LIB%/lwjgl-2.8.0/jar/lwjgl.jar;%JPCT_LIB%/lwjgl-2.8.0/jar/lwjgl_util.jar

java %JPCT_JNI% -Xmx128m -classpath bin;%JPCT_CP% HelloWorldOGL
