#!/bin/sh

arangosh --server.endpoint=tcp://127.0.0.1:8999 --server.authentication false </docker-entrypoint-initdb.d/initdb.aql
