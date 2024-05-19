from flask import Flask
from proofApp.models import db
from config import Configuration
from proofApp.api import proofBlueprint

app = Flask(__name__)
app.config.from_object(Configuration)
app.register_blueprint(proofBlueprint)
db.init_app(app)


