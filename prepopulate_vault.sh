#!/bin/sh

sleep 2

export VAULT_TOKEN=root
VAULT_IP=$(getent hosts vault-service | awk '{ print $1 }')
POSTGRES_IP=$(getent hosts postgres-service | awk '{ print $1 }')
export VAULT_ADDR=http://$VAULT_IP:8200

CREATE_ROLE_SQL="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}' INHERIT;"
GRANT_ROLE_SQL="GRANT ro TO \"{{name}}\";"

#Enabeling secrets database
vault secrets enable database

# 
vault write database/config/postgresql \
     plugin_name=postgresql-database-plugin \
     connection_url="postgresql://{{username}}:{{password}}@$POSTGRES_IP/postgres" \
     allowed_roles=readonly \
     username="root" \
     password="rootpassword" \

vault write database/roles/readonly \
      db_name=postgresql \
      creation_statements="$CREATE_ROLE_SQL $GRANT_ROLE_SQL" \
      default_ttl=1h \
      max_ttl=24h

