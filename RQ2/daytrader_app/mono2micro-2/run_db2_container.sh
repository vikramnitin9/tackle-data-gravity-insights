#!/usr/bin/env bash

DB2_CONTAINER_NAME=db2
NETWORK_NAME=daytrader7
VOLUME_NAME=daytrader-db2data
DOCKERFILE=Dockerfile-db2
MAX_WAIT_TIME=600 # timeout in seconds

docker network inspect $NETWORK_NAME
if [ $? == 1 ]; then
    echo "Creating network \"$NETWORK_NAME\""
    docker network create $NETWORK_NAME
fi

docker volume inspect $VOLUME_NAME
if [ $? == 1 ]; then
    echo "Creating volume \"$VOLUME_NAME\""
    docker volume create $VOLUME_NAME
fi

docker image inspect $NETWORK_NAME/$DB2_CONTAINER_NAME
if [ $? == 1 ]; then
    echo "Building container \"$DB2_CONTAINER_NAME\""
    docker build . -f $DOCKERFILE -t $NETWORK_NAME/$DB2_CONTAINER_NAME
fi

echo "Running container \"$DB2_CONTAINER_NAME\""
docker run -it -d \
    --name $DB2_CONTAINER_NAME \
    --network $NETWORK_NAME \
    --privileged=true \
    -p 50000:50000 \
    -e LICENSE=accept \
    -e DB2INST1_PASSWORD=db2inst1 \
    -e DBNAME=tradedb \
    -v $VOLUME_NAME:/database \
    $NETWORK_NAME/$DB2_CONTAINER_NAME

container_elapsed_time=0
sleep_interval=10
#while true; do
while [ "$container_elapsed_time" -lt "$MAX_WAIT_TIME" ]; do
    echo "Waiting for $DB2_CONTAINER_NAME setup to complete"
    docker logs $DB2_CONTAINER_NAME | grep "DB2 container setup complete"
    if [ "$?" == "0" ]; then break; fi
    echo "waiting ... ${container_elapsed_time}s"
    sleep $sleep_interval
    container_elapsed_time=$((container_elapsed_time+sleep_interval))
done
