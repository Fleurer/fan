#!/bin/bash
mvn -Phome clean package
#VERSION_NAME=`grep versionName AndroidManifest.xml |sed 's/.*android:versionName="\([^"]*\)".*/\1/'`
VERSION_NAME=`grep -E -m 1 -o "<version>(.*)</version>" pom.xml | sed -e 's,.*<version>\([^<]*\)</version>.*,\1,g'`
APK_PREFIX=`grep -E -m 1 -o "<apk.prefix>(.*)</apk.prefix>" pom.xml | sed -e 's,.*<apk.prefix>\([^<]*\)</apk.prefix>.*,\1,g'`
PACKAGE=`grep -E -m 1 -o "<groupId>(.*)</groupId>" pom.xml | sed -e 's,.*<groupId>\([^<]*\)</groupId>.*,\1,g'`
echo $VERSION_NAME
echo $APK_PREFIX
echo $PACKAGE
if [ $? -eq 0 ]
then
    PHONES=`adb devices |grep "device$"|awk '{print $1}'`
    for p in $PHONES
    do
        echo "installing to $p ..."
        adb -s $p install -r ./target/$APK_PREFIX-$PACKAGE-$VERSION_NAME.apk
        adb -s $p shell am start -n com.googolmo.fanfou/com.googolmo.fanfou.MainActivity
    done
fi
