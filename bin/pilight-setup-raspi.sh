#!/bin/bash
#
# Installs all nessessary software/packages and configuration on rasperian
#

# install some basic packages
sudo apt-get install -y screen nano mc wget bash-completion git

# get maven
wget http://ftp-stud.hs-esslingen.de/pub/Mirrors/ftp.apache.org/dist/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
tar -xvzf apache-maven-3.2.3-bin.tar.gz
mkdir $HOME/bin
ln -s apache-maven-3.2.3/bin/mvn $HOME/bin/mvn

# get sources
git clone timo@schatzkammer.subluna.org:Dev/git
mv git pilight
