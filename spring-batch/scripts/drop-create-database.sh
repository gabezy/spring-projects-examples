#!/bin/bash
set -e

docker exec postgres psql -f /home/m/workstation/projects/billing-job/src/sql/schema-drop-postgresql.sql -U postgres
docker exec postgres psql -f /home/m/workstation/projects/billing-job/src/sql/schema-create-postgresql.sql -U postgres

