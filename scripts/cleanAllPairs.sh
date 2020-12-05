#!/bin/bash

cd pairs
for dir in e* f*
do
    cd $dir 
    for file in *
    do
        echo $file
        sort -u $file > /tmp/$file
        mv /tmp/$file .
    done
    cd ..
done
cd ..
