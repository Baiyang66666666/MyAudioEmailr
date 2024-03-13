# These classes look very simple, so you might think that passing variables directly through a "dictionary" is
# simpler and more convenient than creating a class. But creating this class ensures that the code will be
# maintainable later (we may find that these few properties alone won't support our functionality, etc.)

# Receive the request form mail
class TTSRequest:
    def __init__(self, emailId:list, emails:list):
        self._emailId = emailId
        self._emails = emails

    @property
    def emailId(self):
        return self._emailId

    @emailId.setter
    def name(self, value):
        self._emailId = value

    @property
    def emails(self):
        return self._emails

    @emails.setter
    def emails(self, value):
        self._emails = value


# Send the response to mail
class TTSResponse:
    def __init__(self, emailId: list, voices: list):
        self._emailId = emailId
        self._voices = voices

    @property
    def emailId(self):
        return self._emailId

    @emailId.setter
    def name(self, value):
        self._emailId = value

    @property
    def voices(self):
        return self._voices

    @voices.setter
    def emails(self, value):
        self._voices = value
