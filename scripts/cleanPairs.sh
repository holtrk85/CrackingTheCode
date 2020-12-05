#!/bin/bash

for file in *
do
    echo $file
    sort -u $file > /tmp/$file
    mv /tmp/$file .
done
