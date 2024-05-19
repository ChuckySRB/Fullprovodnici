from flask import Blueprint, request, jsonify

from proofApp.models import db
from proofApp.models.records import Record
from proofApp.utils.android_config import verify_play_integrity_token
from proofApp.utils.video_code import video_encoder, generate_video_id

proofBlueprint = Blueprint("proof", __name__)


@proofBlueprint.route("/", methods=["GET"])
def HelloProof():
    return "Proof!"


@proofBlueprint.route("/record", methods=["POST"])
def CreateRecord():
    # Get the video file and android tag from the request
    android_tag = request.form.get('android_tag')
    video = request.files.get('video')

    if not android_tag or not video:
        return jsonify({"error": "Android tag and video are required"}), 400

    # Check android_tag
    """
    Verifies the Play Integrity token.

    This code is commented while testing but should be used once the app is published to Google Store to secure the
    video sent is made by the original application
    """
    # android_data = verify_play_integrity_token(android_tag)
    # if not android_data:
    #     return jsonify({"error": "Invalid access"}), 500

    # Read video content
    video_content = video.read()

    # Make a unique code by hashing the video content
    video_code = video_encoder(video_content)

    # Create a unique video identifier (UUID)
    video_id = generate_video_id()

    # Create a new Record instance (Assuming you have a Record model and a database session)
    # Here you should replace the following with actual database code
    new_record = Record(
        android_tag=android_tag,
        video_id=video_id,
        video_code=video_code
    )
    # Add the new record to the database
    db.session.add(new_record)
    db.session.commit()

    # Return the video identifier to the android phone
    return jsonify({"video_id": video_id})


@proofBlueprint.route("/checkRecord", methods=["POST"])
def CheckRecord():
    # Get the video and video identifier from the request
    video_id = request.form.get('video_id')
    video = request.files.get('video')

    if not video_id or not video:
        return jsonify({"error": "Video ID and video are required"}), 400

    # Read video content
    video_content = video.read()

    # Create video code by hashing the video content
    video_code = video_encoder(video_content)

    # Retrieve the record from the database using the video_id
    record = Record.query.filter_by(video_id=video_id).first()

    if not record:
        return jsonify({"error": "Record not found"}), 404

    # Check if the video code from the database is the same as the created code for the video with the same ID
    if record['video_code'] == video_code:
        return jsonify({"match": True})
    else:
        return jsonify({"match": False})
