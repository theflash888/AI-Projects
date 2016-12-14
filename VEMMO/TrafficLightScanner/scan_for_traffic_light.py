#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2
from matplotlib import pyplot as plt

import time

#IMAGE_FILE = 'uoit_faces.png'
#IMAGE_FILE = 'stop_sign_in_scene.png'
#IMAGE_FILE = 'streetview_003.png'
#IMAGE_FILE = 'uoit_002.png'
#IMAGE_FILE = 'uoit_001.png'
#IMAGE_FILE = 'image_left.jpg'
#IMAGE_FILE = 'traffic.png'
#IMAGE_FILE = 'french_stop_sign.png'
#IMAGE_FILE = 'whitby_traffic_lights.png'
#IMAGE_FILE = 'traffic_light_closeup.png'

#IMAGE_FILE = 'traffic_light_zoom.png'
IMAGE_FILE = 'zoom_light_green.png'
#IMAGE_FILE = 'red_light_clear.png'
#IMAGE_FILE = 'green_light_top.png'


img = cv2.imread(IMAGE_FILE)
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
gray = cv2.medianBlur(gray, 5)

img_location = img.copy()

img_stdmap = img.copy()

edges = cv2.Canny(gray, 60,60)

for y in range(0, img.shape[0]):
	for x in range(0, img.shape[1]):
		img_draw = img.copy()
		cv2.rectangle(img_draw,(x,y),(x+20,y+60),(0,0,255),2)
		cv2.circle(img_draw,(x+10,y+10),2,(0,0,255),2)
		cv2.circle(img_draw,(x+10,y+30),2,(0,0,255),2)
		cv2.circle(img_draw,(x+10,y+50),2,(0,0,255),2)
		
		
		#Extract the 3 circles
		msk_top = np.zeros((img.shape[0],img.shape[1]),np.uint8)
		msk_middle = np.zeros((img.shape[0],img.shape[1]),np.uint8)
		msk_bottom = np.zeros((img.shape[0],img.shape[1]),np.uint8)
		msk_std = np.zeros((img.shape[0],img.shape[1]),np.uint8)
		msk_lightbody = np.zeros((img.shape[0],img.shape[1]),np.uint8)
		
		cv2.circle(msk_top,(x+10,y+10),2,(255,255,255),-1)
		cv2.circle(msk_middle,(x+10,y+30),2,(255,255,255),-1)
		cv2.circle(msk_bottom,(x+10,y+50),2,(255,255,255),-1)
		cv2.circle(msk_std,(x,y),2,(255,255,255),-1)
		
		#Extract the traffic light body
		cv2.rectangle(msk_lightbody,(x,y),(x+20,y+60),(255,255,255),-1)
		cv2.circle(msk_lightbody,(x+10,y+10),12,(0,0,0),-1)
		cv2.circle(msk_lightbody,(x+10,y+30),12,(0,0,0),-1)
		cv2.circle(msk_lightbody,(x+10,y+50),12,(0,0,0),-1)
		
		
		view_top = cv2.bitwise_and(img, img, mask=msk_top)
		view_middle = cv2.bitwise_and(img, img, mask=msk_middle)
		view_bottom = cv2.bitwise_and(img, img, mask=msk_bottom)
		view_lightbody = cv2.bitwise_and(img, img, mask=msk_lightbody)
		
		top_gray = cv2.cvtColor(view_top, cv2.COLOR_BGR2GRAY)
		middle_gray = cv2.cvtColor(view_middle, cv2.COLOR_BGR2GRAY)
		bottom_gray = cv2.cvtColor(view_bottom, cv2.COLOR_BGR2GRAY)
		
		top_std = np.std(top_gray)
		middle_std = np.std(middle_gray)
		bottom_std = np.std(bottom_gray)
		
		cv2.imshow('Top Light', view_top)
		cv2.imshow('Middle Light', view_middle)
		cv2.imshow('Bottom Light', view_bottom)
		cv2.imshow('Image Draw', img_draw)
		cv2.imshow('Light Body', view_lightbody)
		
		top_r_hist = cv2.calcHist([view_top],[2],None,[256],[0,256])
		top_g_hist = cv2.calcHist([view_top],[1],None,[256],[0,256])
		top_b_hist = cv2.calcHist([view_top],[0],None,[256],[0,256])
		
		middle_r_hist = cv2.calcHist([view_middle],[2],None,[256],[0,256])
		middle_g_hist = cv2.calcHist([view_middle],[1],None,[256],[0,256])
		middle_b_hist = cv2.calcHist([view_middle],[0],None,[256],[0,256])
		
		bottom_r_hist = cv2.calcHist([view_bottom],[2],None,[256],[0,256])
		bottom_g_hist = cv2.calcHist([view_bottom],[1],None,[256],[0,256])
		bottom_b_hist = cv2.calcHist([view_bottom],[0],None,[256],[0,256])
		
		bottom_r_sum = np.sum(bottom_r_hist[100:])
		bottom_g_sum = np.sum(bottom_g_hist[100:])
		bottom_b_sum = np.sum(bottom_b_hist[100:])
		
		#print (bottom_r_sum, bottom_g_sum, bottom_b_sum)
		
		#top_average = cv2.mean(img, mask=msk_top)
		
		top_mean, top_std = cv2.meanStdDev(img, mask=msk_top)
		middle_mean, middle_std = cv2.meanStdDev(img, mask=msk_middle)
		bottom_mean, bottom_std = cv2.meanStdDev(img, mask=msk_bottom)
		lightbody_mean, lightbody_std = cv2.meanStdDev(img, mask=msk_lightbody)
		
		im_mean, im_std = cv2.meanStdDev(img, mask=msk_std)
		
		s_b = int(10*im_std[0])
		s_g = int(10*im_std[1])
		s_r = int(10*im_std[2])
		
		cv2.circle(img_stdmap,(x,y),2,(s_b, s_g, s_r),-1)
		
		print lightbody_mean
		
		#if bottom_std[0] < 2 and bottom_std[1] < 2 and bottom_std[2] < 2:
		#	print bottom_std
		#	cv2.rectangle(img_location,(x,y),(x+20,y+60),(255,0,255),2)
		
		#if top_std < 5 and middle_std < 5 and bottom_std < 5:
		#	print "TOP: " + str(top_std) + " - MIDDLE: " + str(middle_std) + " - BOTTOM: " + str(bottom_std) + " - G_SUM: " + str(bottom_g_sum)
		#	cv2.rectangle(img_location,(x,y),(x+20,y+60),(255,0,255),2)
		
		if lightbody_std[0] < 15 and lightbody_std[1] < 15 and lightbody_std[2] < 15 and lightbody_mean[2] > 80:
			cv2.rectangle(img_location,(x,y),(x+20,y+60),(255,0,255),2)
		
		cv2.imshow('Found Objects', img_location)
		cv2.imshow('Stdmap', img_stdmap)
		
		#time.sleep(0.001)
		#time.sleep(0.01)
		if cv2.waitKey(1) & 0xFF == ord('q'):
			break

cv2.imwrite('light_location.png', img_location)


#circles = cv2.HoughCircles(img_stdmap, cv2.cv.CV_HOUGH_GRADIENT, 1.5, 90, param1=50, param2=30, minRadius=0, maxRadius=0)

cv2.waitKey(0)
#cv2.destroyAllWindows()

