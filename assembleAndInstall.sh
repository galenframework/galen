
./makeDist.sh

bin=$(find dist -type d | grep galen-bin)

cd $bin
pwd
sudo ./install.sh
