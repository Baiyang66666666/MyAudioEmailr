from http.server import HTTPServer, BaseHTTPRequestHandler

from models import TTSRequest, TTSResponse
import json


class RequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/':
            # Get the json and parse it
            ttsRequest = self.parsingRequestPara()

            print('email_id:', ttsRequest.emailId)
            print('emails:', ttsRequest.emails)

            # Call the function which is used to convert mails to voices
            # voices = convertToVoice(ttsRequest.emails)
            # ttsResponse = TTSResponse(ttsRequest.emailId, voices)

            # Return to when conversion is complete
            # self.sendResponse(ttsResponse)
            # use this before we can return the voice
            self.sendResponse_temp()
        else:
            self.send_error(404)

    def parsingRequestPara(self):
        content_length = int(self.headers.get('Content-Length', 0))
        requestBody = self.rfile.read(content_length).decode('utf-8')
        data = json.loads(requestBody)
        ttsRequest = TTSRequest(data['emailId'], data['emails'])
        return ttsRequest

    def sendResponse(self, ttsResponse):
        response = json.dumps(ttsResponse)
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', len(response))
        self.end_headers()
        self.wfile.write(response.encode('utf-8'))

    def sendResponse_temp(self):
        self.send_response(200)
        self.end_headers()


def startServer():
    httpd = HTTPServer(('localhost', 8080), RequestHandler)
    httpd.serve_forever()
