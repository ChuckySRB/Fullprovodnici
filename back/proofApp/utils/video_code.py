import hashlib
import base64
import os

def video_encoder(video):
    return hashlib.sha256(video).hexdigest()

def generate_video_id():
    return base64.urlsafe_b64encode(os.urandom(16)).decode('utf-8').rstrip('=')