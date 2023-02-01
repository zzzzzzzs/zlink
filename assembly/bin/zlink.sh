usage="Usage: zlink.sh (start|stop)"

# if args length not equal 1, show usage
if [ $# != 1 ];then
  echo $usage
  exit 1
fi

cmd=$1
BIN_DIR=`dirname $0`
BIN_DIR=`cd "$BIN_DIR"; pwd`

# get ZLINK_HOME
ZLINK_HOME=$BIN_DIR/..

source /etc/profile
export JAVA_HOME=$JAVA_HOME
export FLINK_HOME=$FLINK_HOME
export HOSTNAME=`hostname`
export ZLINK_PID_DIR=$ZLINK_HOME/pid
export ZLINK_LOG_DIR=$ZLINK_HOME/logs
export ZLINK_CONF_DIR=$ZLINK_HOME/config
export ZLINK_LIB_JARS=`find  $ZLINK_HOME/lib/  -name  *.jar | xargs |  sed "s/ /:/g"`
export STOP_TIMEOUT=5

# 如果 FLINK_HOME 不存在
if [ -z $FLINK_HOME ];then
	echo "FLINKE_HOME not exist, please check!"
  exit 1
fi

if [ ! -d "$ZLINK_LOG_DIR" ]; then
    mkdir $ZLINK_LOG_DIR
fi

log=$ZLINK_LOG_DIR/zlink-$HOSTNAME.out
pid=$ZLINK_PID_DIR/zlink.pid

cd $ZLINK_HOME

HEAP_INITIAL_SIZE=1g
HEAP_MAX_SIZE=1g
HEAP_NEW_GENERATION_SIZE=500m
# LOG_FILE="-Dlogging.config=classpath:log4j2.xml -Dspring.profiles.active=dev"
LOG_FILE="-Dlogging.config=classpath:log4j2.xml"
CLASS=com.zlink.Zlink

# export ZLINK_OPTS="-server -Xms$HEAP_INITIAL_SIZE -Xmx$HEAP_MAX_SIZE -Xmn$HEAP_NEW_GENERATION_SIZE -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xss512k -XX:+UseG1GC -XX:LargePageSizeInBytes=128m -XX:+PrintGCDetails -Xloggc:$ZLINK_LOG_DIR/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump.hprof"
export ZLINK_OPTS="-server -Xms$HEAP_INITIAL_SIZE -Xmx$HEAP_MAX_SIZE -Xmn$HEAP_NEW_GENERATION_SIZE -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xss512k -XX:+UseG1GC -XX:LargePageSizeInBytes=128m -Xlog:gc*:$ZLINK_LOG_DIR/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump.hprof"
case $cmd in
  (start)
  [ -w "$ZLINK_PID_DIR" ] || mkdir -p "$ZLINK_PID_DIR"

  if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo zlink application running as process `cat $pid`. Stop it first.
        exit 1
      fi
  fi

  echo logging to $log

  exec_command="$LOG_FILE $ZLINK_OPTS -classpath $ZLINK_CONF_DIR:$ZLINK_LIB_JARS $CLASS"
  # nohup $JAVA_HOME/bin/java $exec_command
  nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 &
  echo $! > $pid
  ;;

 (stop)

   if [ -f $pid ]; then
     TARGET_PID=`cat $pid`
     if kill -0 $TARGET_PID > /dev/null 2>&1; then
       echo stopping zlink application, stop normally after $STOP_TIMEOUT seconds
       kill $TARGET_PID
       sleep $STOP_TIMEOUT
       if kill -0 $TARGET_PID > /dev/null 2>&1; then
          echo "zlink application did not stop gracefully after $STOP_TIMEOUT seconds: killing with kill -9"
          kill -9 $TARGET_PID
       fi
     else
       echo no zlink application to stop
     fi
     rm -f $pid
   else
     echo no zlink application to stop
   fi
   ;;

 (*)
   echo $usage
   exit 1
   ;;

esac