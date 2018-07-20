#!/usr/bin/env bash

activate_ignite(){
    sleep 15
    $IGNITE_HOME/bin/control.sh --activate
}

activate_ignite &
$IGNITE_HOME/run.sh