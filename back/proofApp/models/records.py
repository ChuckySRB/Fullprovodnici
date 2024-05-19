from proofApp.models import db
from sqlalchemy import Column, Integer, String, DateTime, Enum, ForeignKey, func, LargeBinary, Text


class Record(db.Model):
    __tablename__ = 'records'

    id = Column(Integer, primary_key=True, autoincrement=True)
    video_id = Column(String(256), nullable=False)
    video_code = Column(String(256), nullable=False)  # A unique code that is made from video
    android_tag = Column(String(256), nullable=False)
    creation_date = Column(DateTime, default=func.now())

    # left to add more optional data...

    def __repr__(self):
        return f"<Record(id={self.id}, video_id='{self.video_id}', date_of_creation={self.creation_date})>"
