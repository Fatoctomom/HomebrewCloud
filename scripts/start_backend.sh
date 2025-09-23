#!/usr/bin/env bash
set -euo pipefail

PI_USER="Khoi-de-boi"
PI_HOST="tangwing"           
APP_DIR="/opt/minicloud/app" 
JAR_NAME="Cloud-0.0.1-SNAPSHOT.jar"
PORT="8080"
SYSTEMD_SERVICE="minicloud.service"  
JAVA_OPTS="-Xms256m -Xmx512m"         

log() {printf "[start-backend] %s\n" "$*"; }

open_terminal() {
    local title="$1"; shift
    local cmd="$*"
    /usr/bin/osascript <<OSA
tell application "Terminal"
  activate
  set newTab to do script "printf '\\e]1;$title\\a'; $cmd"
end tell
OSA
}

start_remote_cmd() {
    if [[-n "$SYSTEMD_SERVICE"]]; then 
        cat << 'EOF'
    set -euo pipefail
sudo systemctl daemon-reload || true
sudo systemctl start '"$SYSTEMD_SERVICE"'
sudo systemctl status --no-pager --lines=10 '"$SYSTEMD_SERVICE"' || true
EOF
    else
    cat << 'EOF'
set -euo pipefail
cd "$APP_DIR"
if pgrep -f '"$JAR_NAME"' >/dev/null 2>&1; then
  echo "Process already running."
else
  nohup java '"$JAVA_OPTS"' -jar '"$JAR_NAME"' > app.log 2>&1 &
  disown
  echo "Started jar in background."
fi
EOF
  fi
}

log "Starting backend on ${PI_HOST}..."

open_terminal "start-remote" \
"ssh -t ${PI_HOST} 'bash -lc \"$(start_remote_cmd)\"'; echo; echo '--- Press Enter to Close ---'; read"

max_wait=30
elapsed=0
ready=0
while (( elapsed < max_wait )); do
    if ssh -o BatchMode=yes -o ConnectTimeout=3 "${PI_HOST}" "bash -lc 'command -v ss >/dev/null && ss -ltn | grep -q \":${PORT}\" || (command -v netstat >/dev/null && netstat -ltn | grep -q \":${PORT}\")'"; then
        ready=1
        break
    fi
    sleep 1
    ((elapsed++))
done

if (( ready == 1 )); then
    log "Backend listening on port ${PORT}."

    usr/bin/osascript -e 'display notification "Backednd is up on port '"${PORT}"'.""'
    exit 0
else
     log "Backend did not open port ${PORT} within ${max_wait}s."

     /usr/bin/osascript -e 'display notification "Backend failed to start on time.""'
     exit 1
fi


