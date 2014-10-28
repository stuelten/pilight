#!/bin/bash
#
# Installs all necessary software/packages and configuration on rasperian
#

# Proceed with all steps
if [[ "$1" == "-y" ]]
then
    shift
    AUTO_PROCEED=true
fi

ask_proceed_step() {
    echo
    if [[ $AUTO_PROCEED ]]
    then
            echo "Proceed: $1"
            return 0
    else
        read -p "$1 (y/n)?" -er proceed
        case "$proceed" in
            y|yes|Y|YES)
                echo "Proceed: $1"
                return 0;;
            *)
                echo "Skip: $1"
                return 1;;
        esac
    fi
}

wait_for_enter() {
    read -p "Press Enter to proceed..." -er dummy
}

if ( ask_proceed_step "Create individual unique ssh keys for this host" )
then
    sudo dpkg-reconfigure openssh-server
fi

if ( ask_proceed_step "Configure WLAN network" )
then
    read -p "Enter WLAN SSID: " -er ssid
    read -p "Enter WLAN passphrase: " -er passphrase

    echo "Create standard interface config..."
    cat interfaces | sed "s^#SSID#^$ssid^g" | sed "s^#PASSPHRASE#^$passphrase^g" > /etc/network/interface
    echo "New network config:"
    echo "-------------------"
    cat /etc/network/interface

    echo "Restart network services to join WLAN..."
    sudo service networking restart
    if ( ping -A -c 3 -w 5 www.heise.de )
    then
        echo "Network setup successful."
    else
        echo "Error accessing network. Press Ctrl+C to abort"
        wait_for_enter
    fi
fi

if ( ask_proceed_step "Install basic packages (nano git screen mc wget bash-completion)" )
then
    sudo aptitude install -y nano git screen mc wget bash-completion
fi

if ( ask_proceed_step "Install maven" )
then
    VERSION=3.2.3
    FILENAME=apache-maven-$VERSION-bin.tar.gz
    URL=http://www.apache.org/dyn/closer.cgi?path=maven/maven-3/$VERSION/binaries/$FILENAME
    TARGET_DIR=apache-maven-$VERSION
    # name wget'ed tar, because wget uses filename from server which are inconsitent sometimes
    wget -O $FILENAME $URL
    tar -xvzf $FILENAME
    mkdir $HOME/bin
    ln -s $TARGET_DIR/bin/mvn $HOME/bin/mvn
fi

if ( ask_proceed_step "Get sources for pilight" )
then
    git clone timo@schatzkammer.subluna.org:Dev/git
    mv git pilight
fi

if ( ask_proceed_step "Configure bash aliases" )
then
    grep "alias l=" $HOME/.bash_rc && echo "Alias already defined" || {
        echo "alias l='ls -al $@'" >> $HOME/.bash_rc
        echo "New .bash_rc:"
        echo "-------------"
        cat $HOME/.bash_rc
    }
fi
