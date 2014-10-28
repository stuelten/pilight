#!/bin/bash
#
# Installs all necessary software/packages and configuration on rasperian
#

# create individual unique ssh keys for this host
sudo dpkg-reconfigure openssh-server

# install WLAN
read -p "Enter WLAN SSID: " -er ssid
read -p "Enter WLAN passphrase: " -er passphrase

# create standard interface config
cat interfaces | sed "s°#SSID#°$ssid" | sed "s°#PASSPHRASE#°$passphrase" > /etc/network/interface

# restart network services to join wlan
sudo service networking restart

# install some basic packages
sudo apt-get install -y screen nano mc wget bash-completion git

# get maven
wget http://www.apache.org/dyn/closer.cgi?path=maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
tar -xvzf apache-maven-3.2.3-bin.tar.gz
mkdir $HOME/bin
ln -s apache-maven-3.2.3/bin/mvn $HOME/bin/mvn

# get sources
git clone timo@schatzkammer.subluna.org:Dev/git
mv git pilight

# setup aliase
echo "alias l='ls -al $@'" >> $HOME/.bash_rc
