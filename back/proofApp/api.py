from flask import Flask, jsonify, request, Blueprint

proofBlueprint = Blueprint("proof", __name__)


@proofBlueprint.route("/", methods=["GET"])
def HelloProof():
    return "Proof!"


@proofBlueprint.route("/record", methods=["POST"])
def CreateRecord():
    # Get the android tag from request
    # Check the android device
    # Get the video from request
    # Make a unique code by adding video pixels
    # Make a uniqeu video identifier
    # Make a new Record() istance with additional data...
    # Add it to the database
    # Return the video identifier to the android phone
    return "End Recording!"


@proofBlueprint.route("/checkRecord", methods=["POST"])
def CheckRecord():
    # Get the video and video identifier from the  request
    # Create video code from the video
    # Check if the video code from video the database is the same a created code for video with same id
    # return true/false
    return "Recording Checked!"