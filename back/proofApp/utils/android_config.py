from google.oauth2 import id_token
from google.auth.transport import requests as grequests


def verify_play_integrity_token(integrity_token, audience):
    """
    Verifies the Play Integrity token.

    Args:
        integrity_token (str): The Play Integrity token received from the client.
        audience (str): The expected audience value (usually your app's package name).

    Returns:
        dict: The decoded token payload if verification is successful.
        None: If verification fails.
    """
    try:
        # Verify the integrity token
        id_info = id_token.verify_oauth2_token(integrity_token, grequests.Request(), audience)

        return id_info
    except ValueError as e:
        # Token is invalid
        print(f"Token verification failed: {e}")
        return None


