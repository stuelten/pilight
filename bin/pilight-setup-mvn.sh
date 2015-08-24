#!/bin/bash
#
# Set maven environment

# directory with this script
BASEDIR=$( dirname "$0" )

export M2_HOME=/home/pi/apache-maven
export PATH=$M2_HOME/bin:$PATH
