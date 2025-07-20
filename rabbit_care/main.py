import sys
import pika
import json


RABBITMQ_HOST = 'localhost'
EXCHANGE_NAME = 'direct_exchange'
QUEUE_NAME = 'rabbit-care-queue'
ROUTING_KEY = 'rabbit_care_key'

import pika

try:
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    print("Connected to RabbitMQ successfully!")
    connection.close()
except:
    print("Failed to connect to RabbitMQ:", sys.exc_info()[0])



def send_location(id, name, longitude, latitude):
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
    channel = connection.channel()

    channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type='direct')

    channel.queue_declare(queue=QUEUE_NAME)
    channel.queue_bind(exchange=EXCHANGE_NAME, queue=QUEUE_NAME, routing_key=ROUTING_KEY)

    message = {
        'id': id,
        'name': name,
        'longitude': longitude,
        'latitude': latitude
    }

    channel.basic_publish(
        exchange=EXCHANGE_NAME,
        routing_key=ROUTING_KEY,
        body=json.dumps(message)
    )

    print(f"Poslata poruka: {message}")
    connection.close()

if __name__ == '__main__':
    print("Sending bunny care center info...")
    # send_location('1', 'Vet Center', 19.8335, 45.2671)
    # send_location('2', 'Rabbit Haven Help Center', 19.8452, 45.2553)
    # send_location('3', 'VetNS', 19.8791, 45.2513)
    # send_location('4', 'HelpingRabbits021', 19.8489, 45.2467)
    # send_location('5', 'HappyRabbitVet', 19.8498, 45.2446)
    # send_location('6', 'Healthy Bunnies', 19.811246, 45.239594)
    send_location('7', 'BunnyCare Clinic', 19.8265, 45.2650)
    # send_location('8', 'Zeko Point NS', 19.8517, 45.2604)
    # send_location('9', 'Nova Vet Bunny Center', 19.8612, 45.2642)
    # send_location('10', 'Rabbit Rescue Hub', 19.8390, 45.2738)
    # send_location('11', 'Zekino Prijateljstvo', 19.8081, 45.2587)
    # send_location('12', 'SafeHaven Rabbits', 19.8584, 45.2412)
    # send_location('13', 'BunnyZone NS', 19.8733, 45.2649)
    # send_location('14', 'Vet for Fluffies', 19.8244, 45.2445)
    # send_location('15', 'ZekoDom', 19.8308, 45.2533)
    # send_location('16', 'Rabbit Angels NS', 19.8676, 45.2567)

    print("All messages are sent.")
