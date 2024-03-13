# These classes look very simple, so you might think that passing variables directly through a "dictionary" is
# simpler and more convenient than creating a class. But creating this class ensures that the code will be
# maintainable later (we may find that these few properties alone won't support our functionality, etc.)

# Receive the request form mail
class TTSRequest:
    def __init__(self, emailId: str, content: str, voiceType: str):
        self._emailId = emailId
        self._content = content
        self._voiceType = voiceType


    @property
    def emailId(self):
        return self._emailId

    @emailId.setter
    def name(self, value):
        self._emailId = value

    @property
    def content(self):
        return self._content

    @content.setter
    def content(self, value):
        self._content = value

    @property
    def voiceType(self):
        return self._voiceType

    @content.setter
    def voice_type(self, value):
        self._voiceType = value

    def to_dict(self):
        return {
            "emailId": self._emailId,
            "content": self._content,
            "voiceType": self._voiceType
        }

class TTSResponse:
    def __init__(self, emailId: str, content: str):
        self._emailId = emailId
        self._content = content


    @property
    def emailId(self):
        return self._emailId

    @emailId.setter
    def name(self, value):
        self._emailId = value

    @property
    def content(self):
        return self._content

    @content.setter
    def content(self, value):
        self._content = value


    def to_dict(self):
        return {
            "emailId": self._emailId,
            "content": self._content
        }
