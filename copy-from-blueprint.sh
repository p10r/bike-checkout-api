#!/bin/bash

TARGET_DIR=$1

cp -r http4k-blueprint $TARGET_DIR
echo created $TARGET_DIR
cd $TARGET_DIR || exit
rm -rf .idea/

MODULE_NAME=${TARGET_DIR##*/}
echo $MODULE_NAME
# using & instead of /, because / is part of TARGET_DIR
# sed allows using any other string that is not part of the input string as delimiter
sed -i -e 's&http4k-blueprint&'$MODULE_NAME'&g' settings.gradle.kts
#this gets created by sed
rm settings.gradle.kts-e