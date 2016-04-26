#!/bin/bash
#
# Installs all necessary software/packages and configuration on rasperian
#

# check if root
if ( ! id | grep -q "uid=0" )
then
    echo "ERROR: Must be root!"
    echo "Try sudo $0"
    exit 1
fi

# directory with this script
BASEDIR=$( dirname "$0" )

# Proceed with all steps
if [[ "$1" == "-y" ]]
then
    shift
    AUTO_PROCEED=true
elif [[ ! -z "$1" ]]
then
    echo "Unknown parameter \"$1\". Aborting."
    exit 1
fi

ask_proceed_step() {
    local proceed="n"
    echo
    if [[ $AUTO_PROCEED ]]
    then
            echo "Auto-proceed: $1"
            return 0
    else
        read -p "$1 (y/n)? " -er proceed
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
    local dummy=""
    echo "$1"
    read -p "Press Enter to proceed..." -er dummy
}

assert_file_readable() {
    local file="$1"
    if ( ! [[ -r "$file" ]] )
    then
        wait_for_enter "$file not readable. Aborting..."
        exit 1
    fi
}
assert_file_writable() {
    local file="$1"
    if ( ! [[ -w "$file" ]] )
    then
        wait_for_enter "$file not writable. Aborting..."
        exit 1
    fi
}

if ( ask_proceed_step "Create individual unique ssh keys for this host" )
then
    dpkg-reconfigure openssh-server
fi

if ( ask_proceed_step "Configure WLAN network" )
then
    read -p "Enter WLAN SSID: " -er ssid
    read -p "Enter WLAN passphrase: " -er passphrase

    echo "Create standard interface config..."
    assert_file_readable "$BASEDIR/interfaces"
    assert_file_writable "/etc/network/interfaces"

    cat $BASEDIR/interfaces | sed "s^#SSID#^$ssid^g" | sed "s^#PASSPHRASE#^$passphrase^g" > /etc/network/interfaces
    chmod 600 /etc/network/interfaces
    echo "New network config:"
    echo "-------------------"
    cat /etc/network/interfaces

    echo "Restart network services to join WLAN..."
    service networking restart
    invoke-rc.d networking restart
    if ( ping -A -c 3 -w 5 www.heise.de )
    then
        echo "Network setup successful."
    else
        wait_for_enter "Error accessing network. Press Ctrl+C to abort"
    fi
fi

BASIC_PACKAGES="nano git screen mc wget bash-completion"
if ( ask_proceed_step "Install basic packages ($BASIC_PACKAGES)" )
then
    aptitude install -y $BASIC_PACKAGES
fi

if ( ask_proceed_step "Install maven" )
then
    aptitude install -y maven
fi

if ( ask_proceed_step "Get sources for pilight from git" )
then
    read -p "Enter git username: " -er username
    git clone "$((username))@schatzkammer.subluna.org/Users/timo/Dev/git/pilight.git"
fi

if ( ask_proceed_step "Get sources for pilight from SVN" )
then
    read -p "Enter SVN username: " -er username
    svn checkout --username "$username" svn://svn.ckc.de/agwebarchitecture/pilight
fi

if ( ask_proceed_step "Configure bash aliases" )
then
    grep "alias l=" $HOME/.bash_rc && echo "Alias already defined" || {
        echo "alias l='ls -al $@'" >> $HOME/.bash_rc
        echo "New .bash_rc:"
        echo "-------------"
        cat $HOME/.bash_rc
        echo "-------------"
    }
fi
