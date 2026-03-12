#!/bin/bash
# Provision Grafana datasources via API

GRAFANA_URL="http://localhost:3000"
ADMIN_USER="admin"
ADMIN_PASSWORD="${GF_SECURITY_ADMIN_PASSWORD:-admin}"

# Wait for Grafana to be ready
sleep 10

# Add Prometheus datasource
curl -X POST "$GRAFANA_URL/api/datasources" \
  -H "Content-Type: application/json" \
  -u "$ADMIN_USER:$ADMIN_PASSWORD" \
  -d '{
    "name": "Prometheus",
    "type": "prometheus",
    "url": "http://prometheus.monitoring.local:9090",
    "access": "proxy",
    "isDefault": true,
    "jsonData": {
      "timeInterval": "15s"
    }
  }' 2>/dev/null || true

# Add Loki datasource
curl -X POST "$GRAFANA_URL/api/datasources" \
  -H "Content-Type: application/json" \
  -u "$ADMIN_USER:$ADMIN_PASSWORD" \
  -d '{
    "name": "Loki",
    "type": "loki",
    "url": "http://loki.monitoring.local:3100",
    "access": "proxy",
    "jsonData": {
      "derivedFields": [
        {
          "datasourceUid": "jaeger",
          "matcherRegex": "traceId=(\\w+)",
          "name": "TraceID",
          "url": "${__value.raw}"
        }
      ]
    }
  }' 2>/dev/null || true

# Add Jaeger datasource
curl -X POST "$GRAFANA_URL/api/datasources" \
  -H "Content-Type: application/json" \
  -u "$ADMIN_USER:$ADMIN_PASSWORD" \
  -d '{
    "name": "Jaeger",
    "type": "jaeger",
    "uid": "jaeger",
    "url": "http://jaeger.monitoring.local:16686",
    "access": "proxy"
  }' 2>/dev/null || true

echo "Datasources provisioned"
