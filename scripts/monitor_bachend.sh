set -eup pipefail

PI_USER="pi"
PI_HOST="tangwing"
SYSTEMD_SERVICE="minicloud.service"     
APP_DIR="/opt/minicloud/app"            
STORAGE_DIR="/opt/minicloud/mnt/storage"

title_escape() { printf "\\e]1;%s\\a" "$1"; }

open_terminal() {
    local title="$1"; shift
    local cmd="$*"
/usr/bin/osascript <<OSA
tell application "Terminal"
  activate
  do script "printf '\\e]1;$title\\a'; $cmd"
end tell
OSA
}

if [[ -n "$SYSTEMD_SERVICE"]]; then
    open_terminal "backend-logs" \
    "ssh -t ${PI_USER}@${PI_HOST} 'bash -lc \"sudo journalctl -u ${SYSTEMD_SERVICE} -f -n 200 || tail -f ${APP_DIR}/app.log || (echo no logs found; bash)\"'"
else
  open_terminal "backend-logs" \
"ssh -t ${PI_USER}@${PI_HOST} 'bash -lc \"cd ${APP_DIR} && (tail -f app.log || (echo app.log not found; bash))\"'"
fi

open_terminal "mnt-storage" \
"ssh -t ${PI_USER}@${PI_HOST} 'bash -lc \"cd ${STORAGE_DIR} && echo In: \$(pwd) && ls -lah && bash --login\"'"