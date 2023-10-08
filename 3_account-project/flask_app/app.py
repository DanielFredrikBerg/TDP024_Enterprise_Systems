from flask import Response, request, redirect, url_for
from logging.config import fileConfig
import os
import json
from flask import Flask
app = Flask(__name__)
current_path = os.path.dirname(__name__)
#data_path = os.path.relpath('data.json', current_path)
from hashlib import sha256
from kafka import KafkaConsumer, KafkaProducer

fileConfig('logging.cfg')

TOPIC_NAME = "rest-requests"
KAFKA_SERVER = "localhost:9092"

producer = KafkaProducer(
    bootstrap_servers = "localhost:9092", # check which ports we're running on.
    api_version = (0, 11, 15)
)


# Helper functions
def load_json(json_file):
    try:
        with open(str(json_file), 'r') as json_file:
            data = json.load(json_file)
        return data
    except Exception as e:
        print('invalid json: %s' % e)
        return None

def get_bank(name_or_key):
    for bank in load_json("banks.json"):
        if bank["name"] == name_or_key or bank["key"] == name_or_key:
            return bank, 200
    return "null", 404
            
    
# /
@app.route('/', methods=['GET'])
@app.route('/list', methods=['GET'])
def home():
    # KAFKA:
    kafka_message = str.encode("All banks returned from /list")
    producer.send(TOPIC_NAME, kafka_message)
    producer.flush()
    if request.method == 'GET':
        return load_json("banks.json"), 200

# /find  bank or key  
@app.route('/find', methods=['GET'])
def find_name():
    print('bank ' + str(request.args.get('bank', type = str)))
    print('key ' + str(request.args.get('key', type = int)))
    name_or_key = request.args.get('bank', type = str) or request.args.get('key', type = int)
    
    print("name_or_key= " + str(name_or_key))
    found_bank, status = get_bank(name_or_key)
    print("found_bank= " + str(found_bank) + ", status= " + str(status))
    # KAFKA:
    kafka_message = str.encode(f"Found Bank or Person {found_bank}")
    producer.send(TOPIC_NAME, kafka_message)
    producer.flush()
    if found_bank == "null":
        return found_bank
    else:
        return found_bank, status

@app.errorhandler(404)
def not_found_error(error):
    kafka_message = str.encode(f"Error 404 something went a piss.")
    producer.send(TOPIC_NAME, kafka_message)
    producer.flush()
    return json.loads('{}'), 404

# if __name__ == '__main__':
#     app.run(debug=True, host='localhost', port=8070)


# ERROR SOLUTIONS:

# invalid json: [Errno 2] No such file or directory: data.json
# Solution: Terminal position and flask app has to be run (flask run) from the same folder where the data.json folder is located. The api file needs to be in the same folder as the portfolio_app.py for some reason? It can't be in the same folder as the __init__ file at least. At the moment of this writing the ONLY folder "flask run" can be run from is: tdp003/portfolio_app/

# Kill app with Ctrl c otherwise risk for error below:
# Solution Error [errno 98] address already in use flask
# ps -fA | grep python
# kill -9 [pid]
