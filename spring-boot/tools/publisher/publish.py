#!/usr/bin/env python
from google.cloud import pubsub_v1
import sys

project_id = sys.argv[1]
topic_name = sys.argv[2]
records = sys.argv[3]


publisher = pubsub_v1.PublisherClient()

topic_path = publisher.topic_path(project_id, topic_name)

with open(records) as f:
    lines = f.readlines()

count = 0
for line in lines:
    line = line.encode("utf-8")
    future = publisher.publish(topic_path, data=line)
    print(future.result())


