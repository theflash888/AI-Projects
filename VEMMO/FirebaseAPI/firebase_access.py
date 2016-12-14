#!/usr/bin/env python
# -*- coding: utf-8 -*-

import requests
import json

print "Here's an example of posting data to Firebase using REST API"

sign_dictionary = {}
sign_dictionary['type'] = 5
sign_dictionary['distance'] = 60
sign_dictionary['real_time_testing'] = "oooo"
sign_dictionary['received'] = True
sign_dictionary['side'] = 0

my_req = requests.post('https://capstone-149617.firebaseio.com/signs.json', data=json.dumps(sign_dictionary))
print my_req
print my_req.text
