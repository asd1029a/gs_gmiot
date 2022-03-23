#!/bin/bash
#
# 설명:  tcp 구동 스크립트
# 작성자: liu

# Source function library.
[ -f "/etc/rc.d/init.d/functions" ] && . /etc/rc.d/init.d/functions
[ -z "$JAVA_HOME" -a -x /etc/profile.d/java.sh ] && . /etc/profile.d/java.sh

#프로파일
PROFILE=dev
#서비스 포트
PORT=8500
#프로젝트 이름
PROJECT_NAME=danusys
#어플리케이션 이름
#APP_NAME=danusys-tcp-server
# 서비스 실행 사용자
SERVICE_USER=danusys
# jar 파일이 설치된 디렉토리
APP_HOME=/home/${SERVICE_USER}/${APP_NAME}/${PORT}
export APP_HOME
# 어플리케이션 버전
APP_VER="$2"
# 어플리케이션 CLASS 이름
APP_CLASS_NAME=TcpApplication
# jar 파일
APP_JAR="${APP_HOME}/${APP_NAME}-${APP_VER}.jar"
# java home
APP_JAVA=${JAVA_HOME}/bin/java
# 로그 파일
#LOG="/home/${PROJECT_NAME}/${APP_NAME}-${PORT}.log"
LOG="${APP_HOME}/log/${APP_NAME}-${PORT}.log"
# lock file
LOCK="/home/${PROJECT_NAME}/lock/subsys/${PROJECT_NAME}-${APP_NAME}-${PORT}"
# java options
JAVA_OPT="-Xms1024m -Xmx1024m -XX:MaxPermSize=256m -server"

RETVAL=0

pid_of_app() {
    pgrep -f "java.*${APP_JAR}.*server.port=${PORT}"
}

start() {
    [ -e "${LOG}" ] && cnt=`wc -l "${LOG}" | awk '{ print $1 }'` || cnt=1

    echo "Active profile:${PROFILE}"
    echo -n $"Starting ${PROJECT_NAME}:${PORT} Class : ${APP_CLASS_NAME}"

    cd "${APP_HOME}"
    su ${SERVICE_USER} -c "nohup ${APP_JAVA} ${JAVA_OPT} -jar \"${APP_JAR}\" --server.port=${PORT} --spring.profiles.active=${PROFILE} >> \"${LOG}\" 2>&1 &"

    echo ''
    echo -n '['

    tail -n 1 -f ${LOG} | while read line; do
        if [[ $line =~ "Started ${APP_CLASS_NAME}" ]]; then
            pkill -9 -P $$ tail
        else
            echo -n "."
        fi
    done

    echo ']'

    echo "SUCCESS"

}

stop() {
    echo -n "Stopping ${PROJECT_NAME}:${PORT} "

    pid=`pid_of_app`
    [ -n "${pid}" ] && kill ${pid}
    RETVAL=$?
    cnt=10
    while [ ${RETVAL} = 0 -a $cnt -gt 0 ] &&
        { pid_of_app > /dev/null ; } ; do
            sleep 1
            ((cnt--))
    done

    [ ${RETVAL} = 0 ] && rm -f "${LOCK}"
    #[ ${RETVAL} = 0 ] && success $"${STRING}" || failure $"${STRING}"
    echo
}

status() {
    pid=`pid_of_app`
    if [ -n "${pid}" ]; then
        echo "${PROJECT_NAME}:${PORT} (pid ${pid}) is running..."
        return 0
    fi
    if [ -f "${LOCK}" ]; then
        echo $"${base} dead but subsys locked"
        return 2
    fi
    echo "${PROJECT_NAME}:${PORT} is stopped"
    return 3
}


if [ $# -lt 2 ]
then
  echo $"Usage: {start|stop|restart|status} {version}"
  exit 1
fi

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    restart)
        stop
        start
        ;;
    *)
        echo $"Usage: {start|stop|restart|status} {version}"
        exit 1
esac

exit ${RETVAL}
