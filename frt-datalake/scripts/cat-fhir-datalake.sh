#!/bin/bash
set -e
hadoop fs -cat /user/cloudera/frt/datalake/fhir/patient $1
