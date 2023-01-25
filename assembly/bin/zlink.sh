usage="Usage: zlink.sh (start|stop) <command> "

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

startStop=$1
shift
command=$1
shift

echo "Begin $startStop $command......"

BIN_DIR=`dirname $0`
BIN_DIR=`cd "$BIN_DIR"; pwd`
AIPLATFORM_HOME=$BIN_DIR/..

source /etc/profile

export JAVA_HOME=$JAVA_HOME
export HOSTNAME=`hostname`

export AIPLATFORM_PID_DIR=$AIPLATFORM_HOME/pid
export AIPLATFORM_LOG_DIR=$AIPLATFORM_HOME/logs
export AIPLATFORM_CONF_DIR=$AIPLATFORM_HOME/config
export AIPLATFORM_LIB_JARS=`find  $AIPLATFORM_HOME/lib/  -name  *.jar | grep -v aiplatform-scheduler-1.0-SNAPSHOT.jar  | xargs |  sed "s/ /:/g"`

export STOP_TIMEOUT=5

if [ ! -d "$AIPLATFORM_LOG_DIR" ]; then
    mkdir $AIPLATFORM_LOG_DIR
fi

log=$AIPLATFORM_LOG_DIR/$command-$HOSTNAME.out
pid=$AIPLATFORM_PID_DIR/$command.pid

cd $AIPLATFORM_HOME

if [ "$command" = "aiplatform" ]; then
    HEAP_INITIAL_SIZE=1g
    HEAP_MAX_SIZE=1g
    HEAP_NEW_GENERATION_SIZE=500m
    LOG_FILE="-Dlogging.config=classpath:logback-api.xml -Dspring.profiles.active=dev"
    CLASS=com.hongshan.aiplatform.api.ApiApplicationServer
else
  echo "Error: No command named $command was found."
  exit 1
fi

export AIPLATFORM_OPTS="-server -Xms$HEAP_INITIAL_SIZE -Xmx$HEAP_MAX_SIZE -Xmn$HEAP_NEW_GENERATION_SIZE -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xss512k -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCDetails -Xloggc:$AIPLATFORM_LOG_DIR/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump.hprof"

case $startStop in
  (start)
  [ -w "$AIPLATFORM_PID_DIR" ] || mkdir -p "$AIPLATFORM_PID_DIR"

  if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo $command running as process `cat $pid`. Stop it first.
        exit 1
      fi
  fi

  echo starting $command, logging to $log

  exec_command="$LOG_FILE $AIPLATFORM_OPTS -classpath $AIPLATFORM_CONF_DIR:$AIPLATFORM_LIB_JARS $CLASS"

  echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &"
  nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &
  echo $! > $pid
  ;;

 (stop)

   if [ -f $pid ]; then
     TARGET_PID=`cat $pid`
     if kill -0 $TARGET_PID > /dev/null 2>&1; then
       echo stopping $command
       kill $TARGET_PID
       sleep $STOP_TIMEOUT
       if kill -0 $TARGET_PID > /dev/null 2>&1; then
          echo "$command did not stop gracefully after $STOP_TIMEOUT seconds: killing with kill -9"
          kill -9 $TARGET_PID
       fi
     else
       echo no $command to stop
     fi
     rm -f $pid
   else
     echo no $command to stop
   fi
   ;;

 (*)
   echo $usage
   exit 1
   ;;

esac

echo "End $startStop $command."