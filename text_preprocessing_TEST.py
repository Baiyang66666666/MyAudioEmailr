"""
———————————————————————————————————————————————————————————————————————————————————————
                                IMPORTS
———————————————————————————————————————————————————————————————————————————————————————
"""
from bs4 import BeautifulSoup as BS
import nltk
# nltk.download('punkt')
from nltk.corpus import cmudict
# nltk.download('cmudict')
cmu_dict = cmudict.dict()
import re
from num2words import num2words

acronym_list = []
with open('acronym.txt', 'r') as f:
    for line in f:
        acronym_list.append(line.strip('\n'))

"""
———————————————————————————————————————————————————————————————————————————————————————
                                FILE HANDLING
———————————————————————————————————————————————————————————————————————————————————————
"""


def html2txt(text):
    """
    Turns html line to plain text line.
    :param text: html string
    :return: string of html as plain text
    """
    soup = BS(text, "html.parser")
    text = soup.get_text() #.split("\n")
    return text


"""
———————————————————————————————————————————————————————————————————————————————————————
                                MAIN FUNCTIONS
———————————————————————————————————————————————————————————————————————————————————————
"""

def tokenize(line):
    """
    tokenization
    :param line: string
    :return: list of tokens
    """
    new_line = nltk.tokenize.word_tokenize(line)
    # print("tokenized", new_line)
    return new_line


def strip(line):
    """
    stripping useless bits
    e.g. empty test_tok, certain punctuations etc. + lowercase
    :param line: list of tokens
    :return: new list of tokens if input list not empty
    """
    new_line = []
    if line != []:
        for word in line:
            # if not re.match(r"[~`¡⁄‹›†‡·•()—\"’‘“”:;<>{}\[\]\\]|''$", word):
                # do not convert to lowercase if all caps - keep all-cap words for emphasis
            if word.isupper() and word != 'I' and word not in acronym_list:
                new_line.append(word)
            else:
                new_line.append(word.lower())
    if new_line != []:
        # print("stripped", new_line)
        return new_line


def token_parser(line):
    """
    token parsing
    :param line: list of tokens
    :return: new list of tokens
    """
    if line == None:
        return None
    new_line = []
    for i in range(len(line)):
        if line[i] != "":
            if line[i] == "'":
                new_line[-1] += line[i]
            elif line[i][0] == "’":
                new_line[-1] += line[i] + line[i + 1]
                line[i + 1] = ""
            else:
                # token in [",", ".", "?", "!", "…"]:
                new_line.append(line[i])

    return new_line



def text_proc(htmlString):
    """
    pipeline(
            html2txt(),
            tokenize(),
            strip(),
            token_parser(),
    )
    :param htmlString:
    :return: list of pronounceable tokens
    """
    text = html2txt(htmlString)
    script = token_parser(strip(tokenize(text)))
    if script != None:
        if "!" in script:
            script = [word.upper() for word in line if type(word) == "str"]
    # print("script", script)
    return script


