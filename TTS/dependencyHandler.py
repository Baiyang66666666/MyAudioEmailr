"""The function 'get_dep' is called for each dependency when the script is run.
Ref: https://stackoverflow.com/questions/3131217/error-handling-when-importing-modules"""

from importlib import import_module
import os


def get_dep(dep_name):
    try:
        import_module(dep_name)
        print(f"{dep_name} successfully imported.")
    except ImportError:
        print(f"{dep_name} not found. Installing {dep_name}.")
        os.system('pip install '+dep_name)
    else:
        print(f"{dep_name} is already installed.")

# these are all the packages that we need
dep_list = ["pkg_resources", "--upgrade setuptools", "nltk",
            "num2words", "numpy==1.21.4", "gTTs", "soundfile",
            "io", "os", "librosa", "re", "json", "bs4",
            "emoji", "http.server", "Numba==0.53.0"]


for dep in dep_list:
    get_dep(dep)
