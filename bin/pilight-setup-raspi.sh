#!/bin/bash
#
# Installs all necessary software/packages and configuration on rasperian
#

# create individual unique ssh keys for this host
sudo dpkg-reconfigure openssh-server
read -p "Press Enter" -er dummy

# install WLAN
read -p "Enter WLAN SSID: " -er ssid
read -p "Enter WLAN passphrase: " -er passphrase

# create standard interface config
cat interfaces | sed "s°#SSID#°$ssid" | sed "s°#PASSPHRASE#°$passphrase" > /etc/network/interface
cat /etc/network/interface
read -p "Press Enter" -er dummy

# restart network services to join wlan
sudo service networking restart
read -p "Press Enter" -er dummy

# install some basic packages
sudo apt-get install -y screen nano mc wget bash-completion git
read -p "Press Enter" -er dummy

# get maven
wget http://www.apache.org/dyn/closer.cgi?path=maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
tar -xvzf apache-maven-3.2.3-bin.tar.gz
mkdir $HOME/bin
ln -s apache-maven-3.2.3/bin/mvn $HOME/bin/mvn
read -p "Press Enter" -er dummy

# get sources
git clone timo@schatzkammer.subluna.org:Dev/git
mv git pilight
read -p "Press Enter" -er dummy

# setup aliase
echo "alias l='ls -al $@'" >> $HOME/.bash_rc
read -p "Press Enter" -er dummy
