"""
Prosody, timing and diphone concatenation.

USE: prosody_and_synth.py DOC_LIST SPEED

DOC_LIST: list of emails (html files)
SPEED: float

"""

import Constant
import G2P
from git import Repo
import os
import string
import numpy as np
import wave
import re
import soundfile as sf
import resampy
from text_preprocessing import text_proc as tp

# get a list of diphones corresonding to the phonemes
def load_phones(emailContent):
    """Opens and then pre-processes a file. Then calls the g2p function to convert the
    string to a list of phonemes per word (list of lists). Start and end pauses are
    inserted into a word's phoneme list, and phonemes (including the pauses) are then
    joined to form diphones.
    :param html_file: a html document (e.g. an email)
    :return: list of strings, with each string representing a diphone"""
    processed_text = tp(emailContent)

    dp_list = []
    for line in processed_text:
        for word in line:
            word_phons = ['pau'] + G2P.g2p(word) + ['pau']
            for index in range(len(word_phons) - 1):
                diphone = word_phons[index] + '-' + word_phons[index + 1]
                dp_list.append(diphone)
    return dp_list

#This function takes in the list of diphones produced by load_phones() and
# a speed parameter and returns a list of numpy arrays, each corresponding to the audio waveform of a diphone.
def load_wavs(dp_list, speed):
    """Firstly, clones or finds the directory of wavs. Then searches the directory for
    wav files that match the provided diphones. Wav files are then converted into arrays,
    for processing and concatenation.
    :param dp_list: a list of strings representing diphones that is returned by load_phones() function
    :param speed: float that controls dictation speed
    :return: a list of arrays of type np.int16 (converted from wav)"""
    if os.path.isdir(os.curdir + '/diphones'):
        wav_files = os.listdir(os.curdir + '/diphones/diphones')
    else:
        target_path = os.curdir + '/diphones'
        Repo.clone_from('https://github.com/Ternence/NLTKSpeaker', target_path)
        wav_files = os.listdir(target_path + '/diphones')

    wav_list = []

    for diphone in dp_list:
        # search for wav files that match the diphones
        dp_no_digit = diphone.translate(str.maketrans('', '', string.digits))
        if dp_no_digit.lower() + '.wav' in wav_files:
            wav_name = wav_files[wav_files.index(dp_no_digit.lower() + '.wav')]
            wav_path = os.path.join(os.curdir + '/diphones/diphones', wav_name)
            wav_array, fs = sf.read(wav_path, dtype=np.int16)
            wav_faster = resampy.resample(wav_array, sr_orig=fs, sr_new=fs / speed)
            shifted_array = lexical_stress(diphone, wav_faster, fs)

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
            wav_array = resampy.resample(wav_array, sr_orig=fs, sr_new=fs / 1.1)
            wav_array *= 1.1  # amplitude
        elif digit == '2':  # secondary stress
            wav_array = resampy.resample(wav_array, sr_orig=fs, sr_new=fs / 1.05)
            wav_array *= 1.05
        else:
            pass
    else:
        pass
    return wav_array

# get a string representing the path to the output synthesized wave file
def synth(wav_list, wav_name, fs):
    """Concatenates arrays. An audio object is created from the concatenated array and then played.
    :param wav_list: list of arrays returned by load_wavs() function, each array
    corresponding to the np.int16 encoding of a diphone wav file
    :param wav_name: name of wav file
    :param fs: an int, which is the second object returned by load_wavs(), and
    represents the sampling rate of the diphone wavs
    :return: wav file"""

    concat_wavs = np.concatenate(wav_list)
    file_path = Constant.RESOURCE_PATH + wav_name + ".wav"
    with wave.open(file_path, 'w') as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(fs)
        f.writeframes(concat_wavs.astype(np.int16))
    return file_path

#  the main entry point for the module
def convertToVoice(emailId: str, emailContent: str):
    """
    Receive the content of the email
    and return the corresponding audio file address
    :param emailId: id of the email
    :param emailContent
    :return: path to the wav file 
    """
    dp_list = load_phones(emailContent)
    wav_list, fs = load_wavs(dp_list, 1)
    wav_obj = synth(wav_list, emailId, fs)
    wav_path = os.path.join(os.curdir, wav_obj)
    return wav_path
