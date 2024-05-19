from proofApp.models import db
from sqlalchemy import Column, Integer, String, DateTime, Enum, ForeignKey, func, LargeBinary, Text


class Record(db.Model):
    __tablename__ = 'records'

    id = Column(Integer, primary_key=True, autoincrement=True)
    number = Column(String(50))
    video_code = Column(Integer()) # A unique code that is made from video
    creation_date = Column(DateTime)
    app = Column(String(500), default="")
    watermark = Column(String(500), default="")
    android_source = Column(String(50))

    def __repr__(self):
        return f"<Record(id={self.id}, number='{self.number}', date_of_creation={self.creation_date})>"
