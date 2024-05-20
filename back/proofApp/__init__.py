from flask import Flask
from proofApp.models import db
from config import Configuration
from proofApp.api import proofBlueprint
from flask_cors import CORS

app = Flask(__name__)
app.config.from_object(Configuration)
app.register_blueprint(proofBlueprint)
CORS(app, supports_credentials=True)
db.init_app(app)


