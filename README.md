# Android Video Upload and Flask Video Verifier

This repository contains a Flask application that handles video uploads and verifies the integrity of the videos. The app uses Blueprints for modular organization and provides endpoints to upload and check videos.

## Features

- **Record Video**: Upload a video with an associated Android tag.
- **Verify Video**: Verify the integrity of an uploaded video using a unique video identifier.

## Requirements

- Python 3.6+
- Flask
- SQLAlchemy
- GoogleAuth
- Anglular
- MySQL
- Android 24



## Usage

1. **Run the Flask application**:
    ```sh
    python app.py
	```
	
2. **Run the app on your Anroid Phone**:
	- Record a video and save it to the Flask Database
3. **Run the Angular web page:**
	- Upload a video with a video Id and check if the video is valid


## API Endpoints

### `GET /`

Returns a simple message to confirm the service is running.

**Request**:
```sh
curl -X GET http://127.0.0.1:5000/proof/


**Response**:
```plaintext
Proof!
```

### `POST /record`

Uploads a video and returns a unique video identifier.

**Request**:
```sh
curl -X POST http://127.0.0.1:5000/record -F "android_tag=test_tag" -F "video=@your_video"
```

**Response**:
```json
{
  "video_id": "unique_video_identifier"
}
```

### `POST /checkRecord`

Verifies the integrity of an uploaded video using the video identifier.

**Request**:
```sh
curl -X POST http://127.0.0.1:5000/checkRecord -F "video_id=your_video_id" -F "video=@path_to_your_video_file"
```

**Response** (match):
```json
{
  "match": true
}
```

**Response** (no match):
```json
{
  "match": false
}
```

## Directory Structure

```plaintext

│
├── back             	# Flask Backend application
├── android           	# Android Project
├── angular         	# Angular Project for Judges to verify the video
└── README.md   
```

