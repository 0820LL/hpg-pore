gcc -std=gnu99 -D_LARGEFILE64_SOURCE=1 org_opencb_hadoop_pore_NativePoreSupport.c utils.c ../third-party/hdf5-1.8.14/src/*.c ../third-party/hdf5-1.8.14/hl/src/*.c -shared -fPIC -o libopencb_pore.so -I . -I /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.71.x86_64/jre/include/ -I /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.71.x86_64/jre/include/linux/ -I ../third-party/hdf5-1.8.14/src/ -I ../third-party/hdf5-1.8.14/hl/src/