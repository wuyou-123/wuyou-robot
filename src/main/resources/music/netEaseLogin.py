import requests
import sys


response = requests.post(sys.argv[1], allow_redirects=False)
print(response.json())
