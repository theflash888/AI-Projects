#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2
from matplotlib import pyplot as plt

#VIDEO_FILENAME = 'youtube_dashcam.mp4'
VIDEO_FILENAME = 'scaled_down.mp4'

cap = cv2.VideoCapture(VIDEO_FILENAME)
sign_cascade = cv2.CascadeClassifier('frontal_stop_sign_cascade.xml')

rectangles_mask = None
prev_rectangles_mask = None

while(cap.isOpened()):
	ret, frame = cap.read()
	
	if rectangles_mask is None:
		rectangles_mask = np.zeros((frame.shape[0],frame.shape[1]),np.uint8)
		prev_rectangles_mask = np.zeros((frame.shape[0],frame.shape[1]),np.uint8)
		
	
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	
	frame_draw = frame.copy()
	
	
	temp_rectangles_mask = np.zeros((frame.shape[0],frame.shape[1]),np.uint8)
	
	#signs = sign_cascade.detectMultiScale(gray, 1.2)
	signs = sign_cascade.detectMultiScale(gray)
	
	for (x,y,w,h) in signs:
		msk_stopsign = np.zeros((frame.shape[0],frame.shape[1]),np.uint8)
		cv2.rectangle(msk_stopsign,(x,y),(x+w,y+h),(255,255,255),-1)
		sign_mean, sign_std = cv2.meanStdDev(frame, mask=msk_stopsign)
		
		if sign_mean[2] > 100:
			cv2.rectangle(frame_draw,(x,y),(x+w,y+h),(0,0,255),2)
			cv2.rectangle(temp_rectangles_mask,(x,y),(x+w,y+h),(80,80,80),-1)
		
		
	
	#rectangles_mask = cv2.add(rectangles_mask, temp_rectangles_mask)
	
	rectangles_mask = cv2.addWeighted(temp_rectangles_mask, 0.6, rectangles_mask, 0.4, 0)
	
	#prev_rectangles_mask = rectangles_mask.copy()
	
	cv2.imshow('frame',frame_draw)
	cv2.imshow('rectanglesmask', rectangles_mask)
	
	if cv2.waitKey(1) & 0xFF == ord('q'):
		break

cap.release()
cv2.destroyAllWindows()
