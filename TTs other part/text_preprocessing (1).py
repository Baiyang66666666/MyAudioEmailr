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

"""
———————————————————————————————————————————————————————————————————————————————————————
                                FILE HANDLING
———————————————————————————————————————————————————————————————————————————————————————
"""
# HTML to TXT
# files = []
# for i in range(len(htmls)):
#     soup = BS(htmls[i])
#     with open(globals()["sample" + str(i)], “w’) as file:
#         file.write(soup.get_text())
#         files.append(file)
#
# for file in files:
#     lines = []

# TXT
files = [
    "BBSRC DTP Diverse Talent Scholarships available - 2023.txt",
    "Monthly legionella water temperature checks 2023.txt",
    "Reminder about your upcoming booking at Information Commons.txt",
    "punct_test.txt"
]

for i in range(len(files)):
    lines = []
    sample = open("email_samples/"+files[i])
    read_lines = sample.readlines()
    for line in read_lines:
        lines.append(line.strip())
    globals()["sample" + str(i)] = lines
    sample.close()

"""
———————————————————————————————————————————————————————————————————————————————————————
                                MAIN FUNCTIONS
———————————————————————————————————————————————————————————————————————————————————————
"""

punc2read = {
    ".":"dot",
    "@":"at",
    "#":"hash",
    "$":"dollars",
    "£":"british pounds",
    "€":"euros",
    "%":"percents",
    "&":"and",
    "*":"asterix",
    # "(":"open parenthesis",
    # ")":"close parenthesis",
    "_":"underscore",
    "-":"dash",
    "+":"plus",
    "=":"equal",
    "^":"sir cum flex",
    ":":"colon",
    "/":"slash",
}

punc4math = {
    "dash":"minus",
    "dot":"point",
    "asterix":"times",
    "sir cum flex":"to the power of"
}

"""
tokenization
"""
def tokenize(list_of_lines):
    tokenized = []
    for line in list_of_lines:
        tokenized.append(nltk.tokenize.word_tokenize(line))
    return tokenized

"""
stripping useless bits
e.g. empty lines, empty test_tok, certain punctuations etc. + lowercase
"""
def strip(list_of_lines):
    stripped = []
    for line in list_of_lines:
        if line != []:
            stripped_line = []
            for word in line:
                if not re.match(r"[~`()\":<>{}\[\]\\]|''$", word):
                    stripped_line.append(word.lower())
        else:
            continue
        stripped.append(stripped_line)
    return stripped

"""
token parsing
"""
def token_parser(sentence):
    new_sent = []
    for i in range(len(sentence)):
        token = sentence[i]
        if token in [",", ".", "?", "!", "..."]:
            new_sent.append(token)
            continue
        while len(token)>0:
            if re.match(r"^[a-zA-Z]+|^\d+", token):
                match = re.match(r"^[a-zA-Z]+|^\d+", token).group(0)
                new_sent.append(match)
                token = re.sub(r"^[a-zA-Z]+|^\d+", "", token)
            if len(token)>0:
                if token[0] in punc2read.keys():
                    new_sent.append(punc2read[token[0]])
                    token = token[1:]
                elif token[0] == "'":
                    new_sent[-1] += token
                    token = ""
    return new_sent

"""
process math-related punctuations
"""
def math_parser(sentence):
    new_sent = []
    if sentence[1].isdecimal() and (sentence[0] == "dash" or sentence[0] == "dot"): # dash, dot
        new_sent.append(punc4math[sentence[0]])
    else:
        new_sent.append(sentence[0])
    for i in range(1,len(sentence)-1):
        if sentence[i-1].isdecimal() and sentence[i+1].isdecimal() and sentence[i] in punc4math.keys():
            new_sent.append(punc4math[sentence[i]])
        else:
            new_sent.append(sentence[i])
    new_sent.append(sentence[-1])
    return new_sent

"""
pronounce numbers
"""
def num_parser(sentence):
    new_sent = []
    number = []
    for i in range(len(sentence)):
        if sentence[i].isdecimal():
            if len(sentence[i]) <= 4:
                number = num2words(float(sentence[i]))
                if i < len(sentence)-1:
                    if sentence[i+1] in ["st", "nd", "rd", "th"]:
                        number = num2words(float(sentence[i]), to="ordinal")
                number = number.split(" ")
            else:
                for digit in sentence[i]:
                    number.append(num2words(digit))
            new_sent += number
        elif sentence[i] not in ["st", "nd", "rd", "th"]:
            new_sent.append(sentence[i])
    return new_sent

"""
word parsing
look for free morphemes in conjoined words
"""
def word_parser(word):
    if word in cmu_dict:
        return [word]
    letter_count = 0
    morphemes = []
    while word not in cmu_dict and letter_count <= len(word):
        letter_count += 1
        if word[0] in punc2read.keys():
            word = word[1:]
            letter_count = 1
        if letter_count == len(word)-1:
            morphemes += list(word)
            return morphemes
        else:
            if word not in cmu_dict and word[:-letter_count] in cmu_dict:
                morphemes.append(word[:-letter_count])
                word = word[-letter_count:]
                letter_count = 0
            if word in cmu_dict:
                morphemes.append(word)
    return morphemes

"""
pipeline(
        tokenize,
        strip,
        token_parser,
        math_parser,
        num_parser,
        word_parser
)
"""
def text_proc(doc):
    tokenized = tokenize(doc)
    stripped = strip(tokenized)
    tokpar = []
    for line in stripped:
        tokpar.append(token_parser(line))
    mathpar = []
    for line in tokpar:
        mathpar.append(math_parser(line))
    numpar = []
    for line in mathpar:
        numpar.append(num_parser(line))
    wordpar = []
    for line in numpar:
        morphemes = []
        for word in line:
            morphemes += word_parser(word)
        wordpar.append(morphemes)
    return wordpar

"""
———————————————————————————————————————————————————————————————————————————————————————
                                TESTING
———————————————————————————————————————————————————————————————————————————————————————
"""
# sample testing
# test_tok = tokenize(sample1)
# # print(test_tok)
# test_stripped = strip(test_tok)
# # print(test_stripped)
# tokpar = []
# for i in range(len(test_stripped)):
#     print(test_stripped[i])
#     print(token_parser(test_stripped[i]))
#     print(math_parser(token_parser(test_stripped[i])))
#     print(num_parser(math_parser(token_parser(test_stripped[i]))))

# pipeline
# print(text_proc(sample2))
