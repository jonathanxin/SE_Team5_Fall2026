#!/bin/bash
set -e

DB_NAME="photon"
DB_USER="student"
DB_PASSWORD="student"

echo "Setting up PostgreSQL database"

if ! command -v psql >/dev/null 2>&1; then
    echo "PostgreSQL not found. Installing PostgreSQL."
    sudo apt-get update -y
    sudo apt-get install -y postgresql postgresql-client
fi

sudo systemctl enable --now postgresql

# PostgreSQL's system user cannot access some project directories,
# so run its administrative commands from /tmp.
cd /tmp

if ! sudo -u postgres psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER'" | grep -q 1; then
    echo "Creating PostgreSQL user $DB_USER"
    sudo -u postgres psql -c "CREATE ROLE $DB_USER LOGIN PASSWORD '$DB_PASSWORD';"
else
    echo "PostgreSQL user $DB_USER already exists"
    sudo -u postgres psql -c "ALTER ROLE $DB_USER WITH LOGIN PASSWORD '$DB_PASSWORD';"
fi

if ! sudo -u postgres psql -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" | grep -q 1; then
    echo "Creating database $DB_NAME"
    sudo -u postgres createdb -O "$DB_USER" "$DB_NAME"
else
    echo "Database $DB_NAME already exists"
fi

PGPASSWORD="$DB_PASSWORD" psql -h localhost -U "$DB_USER" -d "$DB_NAME" -c \
    "CREATE TABLE IF NOT EXISTS players (id integer, codename character varying(30));"

echo "Database setup complete"
