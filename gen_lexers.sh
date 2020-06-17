#!/bin/bash

for filename in ./src/main/java/syntaxfx/lexers/*; do
    annoflex $filename
done
