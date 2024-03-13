# -*- coding: utf-8 -*-
"""
Prosody, timing and diphone concatenation. A future version will include function
for having email dictation sped up (according to feature 7: "The application shall
allow a user to select specific times when emails should be read").

USE: prosody_and_synth.py DOC

"""
import sys
import G2P
from git import Repo
import os
import string
import numpy as np
import simpleaudio
import re
import soundfile as sf
import resampy
from text_preprocessing import text_proc as tp


def load_phones(text_file):
    """Opens and then pre-processes a file. Then calls the g2p function to convert the
    string to a list of phonemes per word (list of lists). Start and end pauses are
    inserted into a word's phoneme list, and phonemes (including the pauses) are then
    joined to form diphones.
    :param text_file: a document (e.g. an email)
    :return: list of strings, with each string representing a diphone"""

    # assuming that the text is a .txt file:
    f = open(text_file, 'r')
    text = f.readlines()
    processed_text = tp(text)
    dp_list = []
    for line in processed_text:
        for word in line:
            word_phons = ['pau']+G2P.g2p(word)+['pau']
            for index in range(len(word_phons)-1):
                diphone = word_phons[index]+'-'+word_phons[index+1]
                dp_list.append(diphone)
    return dp_list


def load_wavs(dp_list):
    """Firstly, clones or finds the directory of wavs. Then searches the directory for
    wav files that match the provided diphones. Wav files are then converted into arrays,
    for processing and concatenation.
    :param dp_list: a list of strings representing diphones that is returned by load_phones() function
    :return: a list of arrays of type np.int16 (converted from wav)"""
    if os.path.isdir(os.curdir+'/diphones'):
        wav_files = os.listdir(os.curdir+'/diphones/diphones')
    else:
        target_path = os.curdir+'/diphones'
        wav_repo = Repo.clone_from('https://github.com/Ternence/NLTKSpeaker', target_path)
        wav_files = os.listdir(os.curdir+str(wav_repo)+'/diphones')

    wav_list = []

    for diphone in dp_list:
        # search for wav files that match the diphones
        dp_no_digit = diphone.translate(str.maketrans('', '', string.digits))
        if dp_no_digit.lower()+'.wav' in wav_files:
            wav_name = wav_files[wav_files.index(dp_no_digit.lower()+'.wav')]
            wav_path = os.path.join(os.curdir+'/diphones/diphones', wav_name)
            wav_array, fs = sf.read(wav_path, dtype=np.int16)
            shifted_array = lexical_stress(diphone, wav_array, fs)

            wav_list.append(shifted_array)
    # assuming all wav files have the same fs, just return the fs of last wav file
    return wav_list, fs


def lexical_stress(diphone, wav_array, fs):
    """Identifies diphones that are marked for lexical stress. If such a diphone is found,
    its pitch and amplitude are increased, depending on the level of stress.
    :param diphone: str, written in ARPABET, with stress information
    :param wav_array: a diphone wav that has been encoded as a np.int16 array
    :param fs: the original sampling rate of the wav file
    :return: an array that has been changed if its diphone was marked for stress"""
    stress_re = re.compile(r'\d')
    if re.search(stress_re, diphone):
        m = re.search(stress_re, diphone)
        digit = m.group(0)
        if digit == '1':  # primary stress
            wav_array = resampy.resample(wav_array, sr_orig=fs, sr_new=fs/1.1)
            wav_array *= 1.1  # amplitude
        elif digit == '2':  # secondary stress
            wav_array = resampy.resample(wav_array, sr_orig=fs, sr_new=fs/1.05)
            wav_array *= 1.05
        else:
            pass
    else:
        pass
    return wav_array

def synth(wav_list, fs):
    """Concatenates arrays. An audio object is created from the concatenated array and then played.
    :param wav_list: list of arrays returned by load_wavs() function, each array
    corresponding to the np.int16 encoding of a diphone wav file
    :param fs: an int, which is the second object returned by load_wavs(), and
    represents the sampling rate of the diphone wavs
    :return: None"""

    concat_wavs = np.concatenate(wav_list)

    audio_obj = simpleaudio.play_buffer(audio_data=concat_wavs.astype(np.int16),
                            num_channels=1,
                            bytes_per_sample=2,
                            sample_rate=fs)

    audio_obj.wait_done()


def main():
    dp_list = load_phones(sys.argv[1])
    wav_list, fs = load_wavs(dp_list)
    synth(wav_list, fs)

if __name__ == '__main__':
    main()