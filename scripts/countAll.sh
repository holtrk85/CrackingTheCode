#!/bin/bash

let count=0
cd pairs
for dir in *
do
    cd $dir
    for file in *
    do
        let count+=`wc -l $file | gawk '{print $1}'`
    done
    echo $dir $count
    cd ..
done
cd ..
echo Total pairs = $count
