#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2

VIDEO_FILENAME = 'youtube_dashcam.mp4'

cap = cv2.VideoCapture(VIDEO_FILENAME)

while(cap.isOpened()):
	ret, frame = cap.read()
	
	cv2.imshow('frame',frame)
	if cv2.waitKey(1) & 0xFF == ord('q'):
		break

cap.release()
cv2.destroyAllWindows()
