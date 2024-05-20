from config import Configuration
from proofApp import app


@app.route('/')
def hello_world():  # put application's code here
    return 'Hello World!'


if __name__ == '__main__':
    app.run(debug=True, port=Configuration.FLASK_PORT, host=Configuration.FLASK_IP)
