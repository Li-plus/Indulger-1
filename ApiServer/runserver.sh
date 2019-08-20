#!/bin/bash
uwsgi --http :8000 --module indulger.wsgi --processes 4 --threads 2
