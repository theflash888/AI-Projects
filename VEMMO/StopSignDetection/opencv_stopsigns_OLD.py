#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2
from matplotlib import pyplot as plt

#VIDEO_FILENAME = 'youtube_dashcam.mp4'
VIDEO_FILENAME = 'scaled_down.mp4'

cap = cv2.VideoCapture(VIDEO_FILENAME)
sign_cascade = cv2.CascadeClassifier('frontal_stop_sign_cascade.xml')

while(cap.isOpened()):
	ret, frame = cap.read()
	
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	
	#signs = sign_cascade.detectMultiScale(gray, 1.2)
	signs = sign_cascade.detectMultiScale(gray)
	
	for (x,y,w,h) in signs:
		if w > 0 and h > 0:
			sub_image = frame[x:x+w, y:y+h]
			#Find histograms of color channels
			r_hist = cv2.calcHist([sub_image],[2],None,[256],[0,256])
			g_hist = cv2.calcHist([sub_image],[1],None,[256],[0,256])
			b_hist = cv2.calcHist([sub_image],[0],None,[256],[0,256])
			
			r_vari = np.var(r_hist)
			g_vari = np.var(g_hist)
			b_vari = np.var(b_hist)
			
			r_mean = np.mean(frame[2])
			g_mean = np.mean(frame[1])
			b_mean = np.mean(frame[0])
			
			try:
				MAX = max(max(r_hist),max(g_hist),max(b_hist))
				plot = np.zeros((512,1024,3))
				for i in range(255):
					x1 = 4*i
					x2 = 4*(i+1)
					y1 = r_hist[i]*512/MAX
					y2 = r_hist[i+1]*512/MAX
					cv2.line(plot, (x1,y1), (x2, y2), (0,0,255), 3)
					y1 = g_hist[i]*512/MAX
					y2 = g_hist[i+1]*512/MAX
					cv2.line(plot, (x1,y1), (x2, y2), (0,255,0), 3)
					y1 = b_hist[i]*512/MAX
					y2 = b_hist[i+1]*512/MAX
					cv2.line(plot, (x1,y1), (x2, y2), (255,0,0), 3)
				cv2.imshow("histogram", plot)
			except:
				pass
			#cv2.imshow('subimage', sub_image)
			#print (np.mean(r_hist[5:]),np.mean(g_hist[5:]),np.mean(b_hist[5:]))
			#if np.mean(r_hist[5:]) > np.mean(g_hist[5:]) and np.mean(r_hist[5:]) > np.mean(b_hist[5:]):
			#	print "Found Stop Sign"
			#	cv2.rectangle(frame,(x,y),(x+w,y+h),(0,0,255),2)
			#print (r_vari, g_vari, b_vari)
			#print (r_mean, g_mean, b_mean)
			#if r_mean > g_mean and r_mean > b_mean:
			#	cv2.rectangle(frame,(x,y),(x+w,y+h),(0,0,255),2)
			if float(r_mean) / float(g_mean) > 1.2 and float(r_mean) / float(b_mean) > 1.2:
				cv2.rectangle(frame,(x,y),(x+w,y+h),(0,0,255),2)
	
	cv2.imshow('frame',frame)
	if cv2.waitKey(1) & 0xFF == ord('q'):
		break

cap.release()
cv2.destroyAllWindows()
