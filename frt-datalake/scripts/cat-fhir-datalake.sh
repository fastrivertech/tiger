#!/bin/bash
set -e
hadoop fs -cat /user/cloudera/frt/datalake/patient $1
