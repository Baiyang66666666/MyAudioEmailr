# -*- coding: utf-8 -*-
"""
USE: encode_audio.py DIRECTORY

"""

import sys
import json
import os
import wave
import numpy as np

def wav2string(wav_dir):
    # Create an empty dictionary to store the converted audio files
    wav_dict = {}
    # Get a list of all the files in the directory
    wav_list = os.listdir(wav_dir)
    for wav_file in wav_list:
        wav_id = os.path.basename(wav_file)
        wav_path = os.path.join(wav_dir, wav_file)
        # open the audio file
        infile = wave.open(wav_path, 'rb')
        # Read all the frames from the audio file
        frames = infile.readframes(infile.getnframes())
        wav_array = np.frombuffer(frames, dtype=np.int16)
        # converted string to the wav_dict dictionary
        wav_dict.update({wav_id: ", ".join(wav_array.astype(str))})
    # convert the wav_dict to a JSON string
    wav_json = json.dumps(wav_dict)
    return wav_json

def main():
    wav2string(sys.argv[1])

if __name__ == '__main__':
    main()
