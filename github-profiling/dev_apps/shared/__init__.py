import os.path
import sys

root_path = os.path.abspath(os.path.dirname(__file__))
if not root_path in sys.path:
    sys.path.append(root_path)