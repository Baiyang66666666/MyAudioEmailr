from http.server import HTTPServer, BaseHTTPRequestHandler

from models import TTSRequest, TTSResponse
import json
from TTS import tts

# Define a RequestHandler class that inherits from BaseHTTPRequestHandler
class RequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/':
            # Get the json and parse it
            ttsRequest = self.parsingRequestPara()

            print('email_id:', ttsRequest.emailId)
            print('content:', ttsRequest.content)
            print('voiceType', ttsRequest.voiceType)
            # Call the function which is used to convert mail content to voices
            path = tts(ttsRequest.emailId, ttsRequest.content, ttsRequest.voiceType)
            # Create a TTSResponse object with the email ID and path of the voice file
            ttsResponse = TTSResponse(ttsRequest.emailId, path)

            # Return to when conversion is complete
            self.sendResponse(ttsResponse)
        else:
            self.send_error(404)

    # Define the parsingRequestPara() method, which parses the JSON data from the request body into a TTSRequest object
    def parsingRequestPara(self):
        content_length = int(self.headers.get('Content-Length', 0))
        requestBody = self.rfile.read(content_length).decode('utf-8')
        # Parse the JSON data into a dictionary and create a TTSRequest object with the dictionary values
        data = json.loads(requestBody)
        ttsRequest = TTSRequest(data['emailId'], data['content'], data['voiceType'])
        return ttsRequest

    # Define the sendResponse() method, which sends a TTSResponse object as a JSON response to the client
    def sendResponse(self, ttsResponse):
        print(ttsResponse.to_dict())
        response = json.dumps(ttsResponse.to_dict())
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', len(response))
        self.end_headers()
        self.wfile.write(response.encode('utf-8'))

    # Define the sendResponse_temp() method, which sends a temporary response to the client
    def sendResponse_temp(self):
        self.send_response(200)
        self.end_headers()

# Define the startServer() function, which starts an HTTP server on localhost:8080
def startServer():
    httpd = HTTPServer(('localhost', 8080), RequestHandler)
    httpd.serve_forever()
