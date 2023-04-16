#!/usr/bin/env python
import sys
from concurrent.futures import ThreadPoolExecutor
from google.cloud import pubsub_v1

if len(sys.argv) != 4:
    print("Usage: python publish_messages.py <project_id> <topic_name> <records_file>")
    sys.exit(1)

project_id = sys.argv[1]
topic_name = sys.argv[2]
records = sys.argv[3]

batch_settings = pubsub_v1.types.BatchSettings(
    max_bytes=1024,  # Batch messages up to 1KB in size
    max_latency=1,  # Wait up to 1 second before publishing a batch
    max_messages=1000,  # Batch up to 1000 messages
)
publisher = pubsub_v1.PublisherClient(batch_settings=batch_settings)
topic_path = publisher.topic_path(project_id, topic_name)

with open(records) as f:
    lines = f.readlines()

count = 0

def publish_message(line):
    line = line.encode("utf-8")
    publisher.publish(topic_path, line)

with ThreadPoolExecutor(max_workers=5) as executor:
    for line in lines:
        executor.submit(publish_message, line)
        count += 1
        print(count)

print(f"Published {count} messages.")
