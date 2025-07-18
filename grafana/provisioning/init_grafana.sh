#!/bin/bash

echo "Starting Grafana initialization process!"

echo "Checking provisioning files."
#Grafana automatically provisions if the path is like this.
DATASOURCE_FILE='/etc/grafana/provisioning/datasource/prometheus.yml'
DASHBOARD_DIR='/etc/grafana/provisioning/dashboards'

if [ ! -f "$DATASOURCE_FILE" ]; then
    log "WARNING: Data source file $DATASOURCE_FILE not found!"
else
    log "Data source configuration found: $(ls -l $DATASOURCE_FILE)"
fi

if [ ! -d "$DASHBOARD_DIR" ]; then
    log "WARNING: Dashboard directory $DASHBOARD_DIR not found!"
else
    log "Dashboard files found: $(ls -l $DASHBOARD_DIR/*.json 2>/dev/null | wc -l)"
fi


echo "Starting Grafana Server."
exec /run.sh