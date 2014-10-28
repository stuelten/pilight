#!/bin/bash
#
# Installs all necessary software/packages and configuration on rasperian
#

echo "Create individual unique ssh keys for this host..."
sudo dpkg-reconfigure openssh-server
read -p "Press Enter" -er dummy

echo "Configure WLAN network:"
read -p "Enter WLAN SSID: " -er ssid
read -p "Enter WLAN passphrase: " -er passphrase

echo "Create standard interface config..."
cat interfaces | sed "s^#SSID#^$ssid^g" | sed "s^#PASSPHRASE#^$passphrase^g" > /etc/network/interface
echo "New network config:"
echo "-------------------"
cat /etc/network/interface
read -p "Press Enter" -er dummy

echo "Restart network services to join WLAN..."
sudo service networking restart
read -p "Press Enter" -er dummy

echo "Install some basic packages..."
sudo apt-get install -y nano git screen mc wget bash-completion
read -p "Press Enter" -er dummy

echo "Install maven..."
wget http://www.apache.org/dyn/closer.cgi?path=maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
tar -xvzf apache-maven-3.2.3-bin.tar.gz
mkdir $HOME/bin
ln -s apache-maven-3.2.3/bin/mvn $HOME/bin/mvn
read -p "Press Enter" -er dummy

echo "Get sources for pilight..."
git clone timo@schatzkammer.subluna.org:Dev/git
mv git pilight
read -p "Press Enter" -er dummy

echo "Configure bash aliases..."
echo "alias l='ls -al $@'" >> $HOME/.bash_rc
echo "New .bash_rc:"
echo "-------------"
cat $HOME/.bash_rc
read -p "Press Enter" -er dummy
