import requests
import sys

cookies = {
    'p_uin': sys.argv[1],
    'p_skey': sys.argv[2],
    'pt_oauth_token': sys.argv[3],
}

headers = {
    'Content-Type': 'application/x-www-form-urlencoded',
}


def get_gtk(s_key):
    h = 5381
    for c in s_key:
        h += (h << 5) + ord(c)
    return h & 0x7fffffff


data = {
    'src': '1',
    'openapi': '80901010_1030',
    'update_auth': '1',
    'redirect_uri': 'https://y.qq.com/portal/wx_redirect.html',
    'response_type': 'code',
    'client_id': '100497308',
    'g_tk': get_gtk(cookies['p_skey'])
}

response = requests.post('https://graph.qq.com/oauth2.0/authorize', headers=headers, cookies=cookies, data=data, allow_redirects=False)
print(response.headers.get("location"))
