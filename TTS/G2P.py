#Grapheme-to-phoneme (G2P) conversion is the process of converting written words (graphemes) into their corresponding phonemes
#There are several algorithms and libraries available for G2P conversion, I chose the CMU Pronouncing Dictionary
import nltk
from nltk.corpus import cmudict

# Load the CMU Pronouncing Dictionary
nltk.download('cmudict')
cmu_dict = cmudict.dict()

def g2p(word):
    """
    Convert a word from graphemes to phonemes using the CMU Pronouncing Dictionary.
    """
    # Convert the word to lowercase and remove any non-alphabetic characters
    word = ''.join(filter(str.isalpha, word.lower()))

    # If the word is not in the dictionary, return an empty list
    if word not in cmu_dict:
        return []

    # Get the phoneme representation of the word
    phonemes = cmu_dict[word][0]

    # Return the phonemes
    return phonemes



