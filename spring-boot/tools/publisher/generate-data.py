import json
import random

# Function to generate random customer data
def generate_customer_data(id):
    first_name = f"First_{id}"
    last_name = f"Last_{id}"
    email = f"{first_name.lower()}.{last_name.lower()}@example.com"
    phone_number = f"+1-{random.randint(100, 999)}-{random.randint(100, 999)}-{random.randint(1000, 9999)}"
    city = random.choice(["New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"])
    return {
        "id": id,
        "firstName": first_name,
        "lastName": last_name,
        "email": email,
        "phoneNumber": phone_number,
        "city": city
    }

# Function to generate random order data
def generate_order_data(id, customer_id):
    order_date = f"2023-{random.randint(1, 12):02d}-{random.randint(1, 28):02d}"
    items = []
    num_items = random.randint(1, 5)
    for i in range(num_items):
        item_id = i + 1
        product_id = random.randint(1, 100)
        quantity = random.randint(1, 10)
        price = round(random.uniform(1.0, 100.0), 2)
        items.append({
            "id": item_id,
            "productId": product_id,
            "quantity": quantity,
            "price": price
        })
    status = random.choice(["CREATED", "PROCESSING", "COMPLETED", "CANCELED"])
    return {
        "id": id,
        "customerId": customer_id,
        "orderDate": order_date,
        "items": items,
        "status": status
    }

# Get number of customers and orders from user input
num_customers = int(input("Enter number of customers: "))
num_orders = int(input("Enter number of orders: "))

# Generate customer data and write to file
with open(f"customers-{num_customers}.jsonl", "w") as f:
    for i in range(1, num_customers+1):
        customer_data = generate_customer_data(i)
        f.write(json.dumps(customer_data) + "\n")

# Generate order data and write to file
with open(f"orders-{num_orders}.jsonl", "w") as f:
    for i in range(1, num_orders+1):
        customer_id = random.randint(1, num_customers)
        order_data = generate_order_data(i, customer_id)
        f.write(json.dumps(order_data) + "\n")