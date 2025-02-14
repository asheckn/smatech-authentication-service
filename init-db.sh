#!/bin/sh
set -e

psql -U "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "smatech-auth";
    CREATE DATABASE "smatech-store";
    CREATE DATABASE "smatech-payments";
EOSQL
