from flask import Flask
from flask_migrate import Migrate, init, migrate, upgrade
from alas_app.models import db
from alas_app.models.user import User
import alas_app.models.alas
import alas_app.models.socials
from sqlalchemy_utils import database_exists, create_database
from alas_app.utils.auth import sha256_hash
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

    admin = User(
        username = "admin",
        email="admin@admin.com",
        password= sha256_hash("alas123"),
        role="admin"
    )

    db.session.add(admin)
    db.session.commit()
    exit(0)
