#!/bin/bash
# Use Maven Plugin for migration
# In Production, replace these values or pass them as arguments
./mvnw flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/pajak_dthrth \
  -Dflyway.user=admin \
  -Dflyway.password=admin
