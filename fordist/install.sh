#!/bin/bash
set -e

if [ "$(id -u)" != "0" ]; then
    echo "You should run this script as root: sudo $0" 
    exit 1
fi

DEST=/opt

while getopts "d:" OPT; do
    case $OPT in
        d)
            DEST=$OPTARG
            ;;
    esac
done


mkdir -p  $DEST/galen
cp galen.jar $DEST/galen/.
cp galen $DEST/galen


ln -sf $DEST/galen/galen /usr/local/bin/galen

echo "Galen is successfully installed"
echo "You can now check it with the command: galen -v"

