#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd $DIR/../bin
jar cf password.jar com
java -cp password.jar com.md5.view.SearchDatabaseGui
