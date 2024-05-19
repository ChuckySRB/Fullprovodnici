from flask import Flask
from flask_migrate import Migrate, init, migrate, upgrade
from proofApp.models import db
from sqlalchemy_utils import database_exists, create_database
from config import Configuration

application = Flask(__name__)
application.config.from_object(Configuration)

migrateObject = Migrate(application, db)

if not database_exists(application.config["SQLALCHEMY_DATABASE_URI"]):
    create_database(application.config["SQLALCHEMY_DATABASE_URI"])

db.init_app(application)

with application.app_context() as context:
    init()
    migrate(message="Initial migration")
    upgrade()
    db.session.commit()
    exit(0)
