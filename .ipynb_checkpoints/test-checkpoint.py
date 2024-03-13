from TTS import tts

f = open('dolphin_shorter.html', 'rb')
text = f.read()

tts('TTS.test', text, 'en')

