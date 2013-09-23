#!/bin/sh

# Call HTTP GET .../search to populate db with new jobs
# Run daily (with cron?)
curl http://ec2-50-18-26-146.us-west-1.compute.amazonaws.com:8080/JobSeekerServer/search
