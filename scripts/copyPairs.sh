#!/bin/bash

for file in *
do
    echo $file
    cp $file /tmp/$file
    mv /tmp/$file .
done
