#!/bin/bash
# Script to reset the database (Drop and Recreate public schema)
# WARNING: This will DELETE ALL DATA

echo "WARNING: This will delete ALL data in the database."
read -p "Are you sure? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "Aborted."
    exit 1
fi

echo "Resetting database..."
docker exec -i postgres_db psql -U admin -d pajak_dthrth -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

if [ $? -eq 0 ]; then
    echo "Database reset successfully."
else
    echo "Failed to reset database."
    exit 1
fi
