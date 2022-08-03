#!/usr/bin/env bash

echo "(*) Copying DDL into the DB2 container ..."
cp /tmp/tables/Table.ddl /database/config/db2inst1/

echo "(*) Running DDL in the DB2 container ..."
sudo -u db2inst1 bash -c "export PATH=\$PATH:\$HOME/sqllib/bin && export DB2INSTANCE=db2inst1 && db2 connect to tradedb && db2 -tvf \$HOME/Table.ddl"

echo "(*) DB2 container setup complete ..."
