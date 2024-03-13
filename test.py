from TTS import tts

# We write these contents to TTS.test the identification ability of our application
# it includes normal words, punctuations, other language words(French etc.)
text = """<p>Tickets are from &pound;36 and include a red-carpet entry</p>"""

tts('TTS.test', text, 'en')


text_without_comma = 'The sun was setting the sky was painted in shades of orange pink and purple A soft breeze blew through the trees rustling the leaves The sound of a nearby river could be heard as well as the chirping of crickets It was a peaceful scene one that made you feel calm and relaxed'

text_with_comma1 = 'The sun was setting; the sky was painted in shades of orange, pink, and purple. A soft breeze blew through the trees, rustling the leaves.The sound of a nearby river could be heard, as well as the chirping of crickets. It was a peaceful scene, one that made you feel calm and relaxed.'

text_with_comma2 = 'We are happy!!! Some ??? people go to work by bus* and others % may go to work @ by ship$ we are {all good}'

text_with_nonEng = 'The sun was Café setting the sky was Fiancé/Fiancée painted in shades of orange Hors doeuvre pink and Déjà vu purple A soft breeze blew through the trees rustling the leaves The sound of a nearby river could be heard as well as the chirping of crickets It was a peaceful scene one that made you feel calm and relaxed.'

text_long_paragraph = """News from BBC news company The move was "bad for Britain" and marked Microsoft's "darkest day" in its four decades of working in the country Brad Smith told the BBC
The regulator hit back saying it had to do what's best for people "not merging firms with commercial interests"
The UK's move means the multi-billion dollar deal cannot go ahead globally
Although US and EU regulators have yet to decide on whether to approve the deal the UK regulator the Competition and Markets Authority CMA said "Activision is intertwined through different markets - it can't be separated for the UK So this decision blocks the deal from happening globally"
If it had been approved the deal would have been the gaming industry's biggest ever takeover and would have seen Microsoft get hold of massively popular games titles such as Call of Duty Candy Crush and World of Warcraft
"""
#long paragraph is to get how long it will take to generate audio
