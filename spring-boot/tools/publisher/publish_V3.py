#!/usr/bin/env python
from google.cloud import pubsub_v1
import concurrent.futures
import sys

project_id = sys.argv[1]
topic_name = sys.argv[2]
records = sys.argv[3]

publisher = pubsub_v1.PublisherClient()
topic_path = publisher.topic_path(project_id, topic_name)

def publish_message(message):
    future = publisher.publish(topic_path, data=message)
    return future.result()

def main():
    with open(records) as f:
        lines = f.readlines()
        with concurrent.futures.ThreadPoolExecutor(max_workers=2) as executor:
            results = list(executor.map(publish_message, [line.encode("utf-8") for line in lines]))
            for result in results:
                print(result)

if __name__ == '__main__':
    main()