from flask import Flask
from config import Configuration
from proofApp.models import db
from proofApp.models.records import Record
from proofApp.utils.video_code import *

application = Flask(__name__)
application.config.from_object(Configuration)

db.init_app(application)

TEST_FAKE_VIDEO_PATH = "proofApp/database/test_records/Screen_Recording_20240519_210942_WhatsApp_FAKE.mp4"
TEST_REAL_VIDEO_PATH = "proofApp/database/test_records/Screen_Recording_20240519_210942_WhatsApp.mp4"

VIDEO_ID = "to1SxIs_lLoqDMlj-XpKzw"

with application.app_context() as context:  # Read video content
    with open(TEST_FAKE_VIDEO_PATH, 'rb') as f:
        fake_video_content = f.read()
    with open(TEST_REAL_VIDEO_PATH, 'rb') as f:
        real_video_content = f.read()

    fake_video_code = video_encoder(fake_video_content)
    real_video_code = video_encoder(real_video_content)

    # Retrieve the record from the database using the video_id
    record = Record.query.filter_by(video_id=VIDEO_ID).first()

    if not record:
        print("No record Found!")
        exit(0)

    # Check if the video code from the database is the same as the created code for the video with the same ID
    if record.video_code == fake_video_code:
        print("FAKE VIDEO IS REAL!")
    else:
        print("FAKE VIDEO IS FAKE!")

    # Check if the video code from the database is the same as the created code for the video with the same ID
    if record.video_code == real_video_code:
        print("REAL VIDEO IS REAL!")
    else:
        print("REAL VIDEO IS FAKE!")

    exit(0)
