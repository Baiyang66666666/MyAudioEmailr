"""
———————————————————————————————————————————————————————————————————————————————————————
                                IMPORTS
———————————————————————————————————————————————————————————————————————————————————————
"""
from bs4 import BeautifulSoup as BS
import nltk
import langid
# nltk.download('punkt')
from nltk.corpus import cmudict

# nltk.download('cmudict')
cmu_dict = cmudict.dict()
import re
# import emoji
from num2words import num2words
import os
# Creating a list to hold the acronyms
acronym_list = []

# Opening and reading the 'acronym.txt' file
with open('acronym.txt', 'r') as f:
    for line in f:
        acronym_list.append(line.strip('\n'))

"""
———————————————————————————————————————————————————————————————————————————————————————
                                FILE HANDLING
———————————————————————————————————————————————————————————————————————————————————————
"""


# HTML to TXT
# Defining a function to convert HTML line to plain text line
def html2txt(text):
    """
    Turns html line to plain text line.
    :param text:
    :return:
    """
    soup = BS(text, "html.parser")
    text = soup.get_text() #.split("\n")
    return text


"""
———————————————————————————————————————————————————————————————————————————————————————
                                MAIN FUNCTIONS
———————————————————————————————————————————————————————————————————————————————————————
"""
# Creating a dictionary to hold the punctuations and their pronunciation
punc2read = {
    ",": "comma",
    ".": "dot",
    "@": "at",
    "©": "copyright",
    "®": "registered trademark",
    "™": "trademark",
    "#": "hash",
    "$": "dollars",
    "£": "british pounds",
    "€": "euros",
    "%": "percents",
    "&": "and",
    "*": "asterix",
    "°": "degree",
    "_": "underscore",
    "-": "dash",
    "–": "to",
    "+": "plus",
    "±": "plus or minus",
    "=": "equal",
    "≠": "not equal",
    "^": "sir cum flex",
    ":": "colon",
    "/": "slash",
}

# Creating a dictionary to hold the mathematical symbols and their pronunciation
punc4math = {
    "dash": "minus",
    "dot": "point",
    "asterix": "times",
    "sir cum flex": "to the power of"
}

# Defining a function to tokenize each line
def tokenize(line):
    """
    tokenization
    :param list_of_lines: [text["line", "line"...], text[]...]
    :return: [text[line"token", "token"... line"token", "token"...], text[]...]
    """

    return nltk.tokenize.word_tokenize(line)

# Defining a function to strip useless bits
def strip(line):
    """
    stripping useless bits
    e.g. empty lines, empty test_tok, certain punctuations etc. + lowercase
    """
    stripped_line = []
    for word in line:
        if not re.match(r"[~`¡⁄‹›†‡·•()—\"’‘“”:;<>{}\[\]\\]|''$", word):
            # do not convert to lowercase if all caps - keep all-cap words for emphasis
                if word.isupper() and word != 'I' and word not in acronym_list:
                    stripped_line.append(word)
                else:
                    stripped_line.append(word.lower())
        else:
            continue

    return stripped_line

# Defifne a function for token parsing
def token_parser(line):
    """
    token parsing
    """
    new_line = []
    print(line)
    # Iterate over each character in the input line
    for i in range(len(line)):
        token = line[i]
        # If the current token is a punctuation mark, append it to the list and move on to the next token
        if token in [",", ".", "?", "!", "…"]:
            print("punc", token)
            new_line.append(token)
            continue
        # Start parsing when the current token is not a punctuation
        while len(token) > 0:
            print(">0", token)
            # If the token starts with a letter or digit, extract the longest possible sequence of letters/digits as a match
            if re.match(r"^[a-zA-Z]+|^\d+", token):
                match = re.match(r"^[a-zA-Z]+|^\d+", token).group(0)
                new_line.append(match)
                token = re.sub(r"^[a-zA-Z]+|^\d+", "", token)
            # If there are still characters left in the token, check if the first character is a special punctuation mark
            if len(token) > 0:
                if token[0] in punc2read.keys():
                    new_line += punc2read[token[0]].split()
                    token = token[1:]
                elif token[0] == "'":
                    new_line[-1] += token
                    token = ""
    print("tokparsed", new_line)
    return new_line

# replace math-related punctuations with corresponding symbols
def math_parser(line):
    """
    process math-related punctuations
    """
    new_line = []
    if len(line) == 1:
        return line
    # replace dash/dot with corresponding math symbols
    if line[1].isdecimal() and (line[0] == "dash" or line[0] == "dot"):
        new_line.append(punc4math[line[0]])
    else:
        new_line.append(line[0])
    for i in range(1, len(line) - 1):
        if line[i - 1].isdecimal() and line[i + 1].isdecimal() and line[i] == "comma":
            new_line[-1] += line[i + 1]
        elif line[i - 1].isdecimal() and line[i + 1].isdecimal() and line[i] in punc4math.keys():
            new_line.append(punc4math[line[i]])
        else:
            new_line.append(line[i])
    new_line.append(line[-1])
    print("mathparsed", new_line)
    return new_line

# convert numbers to words
def num_parser(line):
    """
    pronounce numbers
    """
    new_line = []
    number = []
    for i in range(len(line)):
        if line[i].isdecimal():
            # Convert numbers to words using num2words library.
            if len(line[i]) <= 4:
                number = num2words(float(line[i]))
                if i < len(line) - 1:
                    # If the next word is a suffix (st, nd, rd, th), convert the number to ordinal.
                    if line[i + 1] in ["st", "nd", "rd", "th"]:
                        number = num2words(float(line[i]), to="ordinal")
                number = number.split("-")
            else:
                for digit in line[i]:
                    number.append(num2words(digit))
            new_line += number
        # If the word is not a number or a suffix, append it to the new line as is.
        elif line[i] not in ["st", "nd", "rd", "th"]:
            new_line.append(line[i])
    print("numparsed", new_line)
    return new_line


def text_proc(htmlString):
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
    text = html2txt(htmlString)
    print("\n html2txt")
    line = strip(tokenize(text))
    print("\n normalized", line)
    new_text = num_parser(math_parser(token_parser(line)))
    print('new text:', new_text)
    # split line into phrases
    phrase_re = re.compile(r"[\w+\s]+[$.,!?]?")
    return re.findall(phrase_re, " ".join(new_text))

"""
———————————————————————————————————————————————————————————————————————————————————————
                                TESTING
———————————————————————————————————————————————————————————————————————————————————————
"""


def test(sample1):
    """
    Test the text processing pipeline on the given text sample.
    """
    test_tok = tokenize(sample1)
    print(test_tok)
    test_stripped = strip(test_tok)
    print(test_stripped)
    for i in range(len(test_stripped)):
        print(test_stripped[i])
        print(token_parser(test_stripped[i]))
        print(math_parser(token_parser(test_stripped[i])))
        print(num_parser(math_parser(token_parser(test_stripped[i]))))
