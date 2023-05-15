## Pubsub Emulator Setup

Run following command :

```bash
docker run  --name pubsub \
-p 8432:8432 \
-e PUBSUB_PROJECT_ID=my-project-id \
-e PUBSUB_CONFIG='[{"name": "orders", "subscriptions": ["orders"]}, {"name": "customers", "subscriptions": ["customers"]}]' \
-d markkrijgsman/pubsub
```

## Setup Publisher

### Run following commands:

```bash
pip install -r requirements.txt
export PUBSUB_EMULATOR_HOST=localhost:8432
```

### Publish transactions

```bash
# python3 publish.py <project_name> <topic_name> <path_to_file>

python3 publish.py my-project-id orders customers.jsonl
```
```bash

python3 publish.py teqnation-2023-stream-proc orders customers.jsonl
```


