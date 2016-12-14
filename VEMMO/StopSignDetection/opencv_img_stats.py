#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2
import sys


img = cv2.imread(sys.argv[1])

sign_mean, sign_std = cv2.meanStdDev(img)

print sign_mean
print sign_std
