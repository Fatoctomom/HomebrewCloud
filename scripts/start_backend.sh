#!/usr/bin/env bash
set -euo pipefail

PI_USER="Khoi-de-boi"
PI_HOST="tangwing"           
APP_DIR="/opt/minicloud/app" 
JAR_NAME="Cloud-0.0.1-SNAPSHOT.jar"
PORT="8080"
SYSTEMD_SERVICE="minicloud.service"  
JAVA_OPTS="-Xms256m -Xmx512m"         