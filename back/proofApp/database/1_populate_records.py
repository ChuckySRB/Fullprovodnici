from flask import Flask
from config import Configuration
from proofApp.models import db
from proofApp.models.records import Record
from proofApp.utils.video_code import *
from datetime import datetime

application = Flask(__name__)
application.config.from_object(Configuration)

db.init_app(application)

TEST_VIDEO_PATH = "test_records/Screen_Recording_20240519_210942_WhatsApp.mp4"

with application.app_context() as context:  # Read video content
    with open(TEST_VIDEO_PATH, 'rb') as f:
        video_content = f.read()
    record = Record(
        video_id = generate_video_id(),
        video_code = video_encoder(video_content),
        android_tag = "TAG_FULLPROVODNIK"
    )

    db.session.add(record)
    db.session.commit()
    exit(0)
