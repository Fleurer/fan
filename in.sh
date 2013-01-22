#!/bin/bash
mvn -Phome clean package
VERSION_NAME=`grep versionName Client/AndroidManifest.xml |sed 's/.*android:versionName="\([^"]*\)".*/\1/'`
echo $VERSION_NAME
#mvn package
if [ $? -eq 0 ]
then
    PHONES=`adb devices |grep "device$"|awk '{print $1}'`
    for p in $PHONES
    do
        echo "installing to $p ..."
        adb -s $p install -r Client/target/googolmo-com.googolmo.shmily-$VERSION_NAME.apk
        adb -s $p shell am start -n com.googolmo.shmily/com.googolmo.shmily.MainActivity
    done
fi
