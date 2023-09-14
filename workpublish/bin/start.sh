#! /bin/bash

#source /etc/profile

# 获取当前脚本所在的目录
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# 指定metaServer jar包的路径
METAJAR_PATH="$DIR/../metaServer"

# 指定dataServer jar包的路径
DATAJAR_PATH="$DIR/../dataServer"



#启动MetaServer master和slave

nohup java -jar "$METAJAR_PATH/metaServer-1.0.jar" > "$METAJAR_PATH/master.log" 2>&1 &

nohup java -jar "$METAJAR_PATH/metaServer-1.0.jar" --spring.profiles.active=slave > "$METAJAR_PATH/slave.log" 2>&1 &


#启动dataServer node1 node2 node3 node4
nohup java -jar "$DATAJAR_PATH/dataServer-1.0.jar" > "$DATAJAR_PATH/node1.log" 2>&1 &

nohup java -jar "$DATAJAR_PATH/dataServer-1.0.jar" --spring.profiles.active=node2 > "$DATAJAR_PATH/node2.log" 2>&1 &

nohup java -jar "$DATAJAR_PATH/dataServer-1.0.jar" --spring.profiles.active=node3 > "$DATAJAR_PATH/node3.log" 2>&1 &

nohup java -jar "$DATAJAR_PATH/dataServer-1.0.jar" --spring.profiles.active=node4 > "$DATAJAR_PATH/node4.log" 2>&1 &

