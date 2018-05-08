#!/bin/bash

## Licensed to the Apache Software Foundation (ASF) under one
## or more contributor license agreements.  See the NOTICE file
## distributed with this work for additional information
## regarding copyright ownership.  The ASF licenses this file
## to you under the Apache License, Version 2.0 (the
## "License"); you may not use this file except in compliance
## with the License.  You may obtain a copy of the License at
##
##     http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.

## Parts of this script have been copied from the corresponding script in
## Apache Jena. This original file can be found at:
##    https://github.com/apache/jena/blob/master/jena-arq/Grammar/grammar
##
##   Olaf Hartig http://olafhartig.de/

DIR=../src/main/java/se/liu/ida/rdfstar/tools/sparqlstar/lang/sparqlstar/
FILE=sparqlstar_11.jj
CLASS=SPARQLStarParser11

rm ${DIR}*

javacc -OUTPUT_DIRECTORY=$DIR -JDK_VERSION=1.7 "${FILE}"



# Fix unnecessary imports
echo "---- Fixing Java warnings in ${NAME}TokenManager ..."

F="$DIR/${CLASS}TokenManager.java"

sed -e 's/import .*//' -e 's/MatchLoop: do/do/' \
    -e 's/int hiByte = (int)(curChar/int hiByte = (curChar/' \
< $F > F
mv F $F

echo "---- Fixing Java warnings in Token ..."

F="$DIR/Token.java"

sed -e 's/@Override //' \
    -e 's/public String toString/@Override public String toString/' < $F > F
mv F $F

echo "---- Fixing Java warnings in TokenMgrError ..."

F="$DIR/TokenMgrError.java"

sed -e 's/@Override //' \
    -e 's/public String getMessage/@Override public String getMessage/' < $F > F
mv F $F

echo "---- Fixing Java warnings in ${CLASS} ..."

F="$DIR/${CLASS}.java"

sed -e 's/public class /\n@SuppressWarnings("all")\npublic class /' < $F > F 

mv F $F

echo "---- Done"
