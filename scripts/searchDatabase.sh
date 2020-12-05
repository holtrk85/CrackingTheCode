#!/bin/bash

cd /usr/local/ZedaSoft/holtrk/prj-3.4/ASRI/DTCC/Apache/1.16.0/test/com/md5/pairs/$1
grep $3 $2 >> ../../search.out
