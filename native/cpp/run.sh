g++ -fPIC -shared -llog -landroid -lEGL -lGLESv1_CM main.cpp cube/cube.cpp -o libnativeegl.so
su -c cp libnativeegl.so /data/app/com.objcplus-1/lib/arm64/
su -c chmod 777 /data/app/com.objcplus-1/lib/arm64/libnativeegl.so

