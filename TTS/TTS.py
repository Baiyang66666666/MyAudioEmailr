"""
Creates the synthesised speech, using gTTS and post-processing.
"""

from gtts import gTTS
from io import BytesIO
import numpy as np
from text_preprocessing import text_proc as tp
import Constant
import os
import soundfile as sf
import librosa

def tts(wav_name: str, email_content: str, accent: str, speed='normal'):
    """ Takes the content of a html file (email) as input. Converts the html to text and then processes the
    text using the text_preprocessing module (team-created). Synthesises an utterance using the
    gtts module (externally-created), and then post-processes the utterance for more human-like speech.
    Finally, creates a wav file from the utterance.
        :param wav_name: a string that will be the name of the wav file output
        :param email_content: html content of an email
        :param accent: string representing localised accent that email will be read in (e.g. 'com.au' for
        Australian English). Defaults to 'us' (American English).
        :param speed: string representing speed of utterance ('slow', 'normal', 'fast'). Defaults to 'normal'.
        :return: a string, which is the file path of the wav file output"""

    # check that accent is supported
    if accent not in ['com.au', 'co.uk', 'us', 'ca', 'co.in', 'ie', 'co.za']:
        raise Exception(f"The selected accent '{accent}' is not supported. Instead choose from one of the following:\n"
                        f"- 'com.au': Australia\n- 'co.uk': United Kingdom\n- 'us': United States\n- 'ca': Canada\n"
                        f"- 'co.in': India\n- 'ie': Ireland\n- 'co.za': South Africa")

    # pre-process the email content
    text = tp(email_content)

    try:
        # synthesise utterance and get speech data (arr) and sampling rate (fs)
        arr_list = []
        fs = 0
        for phrase in text:
            if len([i for i in phrase if i.isalnum()]) < 1:
                arr = np.zeros(30, dtype='float64')
                fs = 24000
            else:
                buffer = BytesIO()
                speech = gTTS(phrase, tld=accent)
                speech.write_to_fp(buffer)
                arr, fs = sf.read(BytesIO(buffer.getvalue()))

            # punctuation handling
            # (+ trim leading and trailing silences to avoid long pauses between lines and reduce file size)
            if phrase[-1] == '!':
                arr *= 2
                arr, _ = librosa.effects.trim(arr, top_db=50, frame_length=256, hop_length=64)
            elif phrase[-1] == '?':
                arr, _ = librosa.effects.trim(arr, top_db=50, frame_length=256, hop_length=64)
            elif phrase[-1] == ',':
                _, idx = librosa.effects.trim(arr, top_db=50, frame_length=256, hop_length=64)
                arr = arr[:idx[1]+800]  # leave short pause
            elif phrase[-1] == '.':
                _, idx = librosa.effects.trim(arr, top_db=50, frame_length=256, hop_length=64)
                arr = arr[:idx[1]+2000]  # leave long pause
            else:
                pass

            arr_list.append(arr)

        data = np.concatenate(arr_list, dtype='float64')

        # set speed (scales fs down or up by 20%)
        if speed == 'slow':
            data = librosa.resample(data, orig_sr=fs, target_sr=int(fs*1.2), res_type='soxr_vhq', fix=False, scale=True)
        elif speed == 'fast':
            data = librosa.resample(data, orig_sr=fs, target_sr=int(fs/1.2), res_type='soxr_vhq', fix=False, scale=True)
        elif speed == 'normal':
            pass

        # write to file and return filepath
        file_path = Constant.RESOURCE_PATH + wav_name + accent + ".wav"

        sf.write(file_path, data, fs)  # write audio to file

        return os.path.join(os.curdir, file_path)

    except AssertionError:
        print("empty list")
        return -1
