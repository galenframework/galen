#!/bin/bash
set -e

if [ "$(id -u)" != "0" ]; then
    echo "You should run this script as root: sudo $0" 
    exit 1
fi

DEST=/opt

while getops "d:" OPT; do
    case $OPT in
        d)
            DEST=$OPTARG
            ;;
    esac
done

exit 1


mkdir  $DEST/galen
cp galen.jar $DEST/galen/.
cp galen $DEST/galen

ln -s $DEST/galen/galen /usr/bin/galen

echo "Galen is successfully installed"

