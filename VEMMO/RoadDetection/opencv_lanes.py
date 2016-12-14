#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import cv2
from matplotlib import pyplot as plt

#VIDEO_FILENAME = 'youtube_dashcam.mp4'
VIDEO_FILENAME = 'scaled_down.mp4'

cap = cv2.VideoCapture(VIDEO_FILENAME)

prev_output = 0.0

while(cap.isOpened()):
	ret, frame = cap.read()
	#print ret
	
	
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	edges = cv2.Canny(gray, 50,350)
	ret, edges_inv = cv2.threshold(edges, 128, 255, cv2.THRESH_BINARY_INV)
	
	#Draw a trapezoid
	mat_trapezoid_mask = np.zeros((gray.shape[0], gray.shape[1]), np.uint8)
	trap_y = gray.shape[0]
	trap_x = gray.shape[1]/2
	#pts = np.array([[trap_x - 50, trap_y - 50],[trap_x + 50, trap_y - 50],[trap_x + 10, trap_y - 250],[trap_x - 10, trap_y - 250]],np.int32)
	#pts = pts.reshape((-1,1,2))
	#cv2.polylines(mat_trapezoid_mask, [pts], True, (255,255,255))
	
	#cv2.rectangle(mat_trapezoid_mask, (trap_x - 100, trap_y - 300), (trap_x + 100 ,trap_y - 50), (255,255,255),-1)
	pts = np.array([[trap_x - 100, 500],[trap_x + 100, 500],[trap_x + 30, 400],[trap_x - 30, 400]], np.int32)
	cv2.fillConvexPoly(mat_trapezoid_mask,pts,1)
	
	
	lane_view = cv2.bitwise_and(frame, frame, mask=mat_trapezoid_mask)
	lane_view_small = lane_view[350:550, trap_x - 150: trap_x + 150]
	
	lane_view_binary = cv2.bitwise_and(edges, edges, mask=mat_trapezoid_mask)
	lane_view_binary_small = lane_view_binary[350:550, trap_x - 150: trap_x + 150]
	
	lane_std = np.std(lane_view_binary_small)
	
	#generate shadow image
	mat_blue = np.zeros((gray.shape[0], gray.shape[1], 3), np.uint8)
	shadow_color = (0,255,0)
	
	#if lane_std > 10:
		#shadow_color = (0,0,255)
	
	danger_output = 0.7*lane_std + 0.3*prev_output
	if danger_output > 10:
		shadow_color = (0,0,255)
	
	prev_output = danger_output
	
	mat_blue[:,:] = shadow_color
	
	blue_shadow = cv2.bitwise_and(mat_blue, mat_blue, mask=mat_trapezoid_mask)
	frame_draw = cv2.add(frame, blue_shadow)
	
	#cv2.imshow('trapmask', mat_trapezoid_mask)
	
	cv2.imshow('frame',frame_draw)
	
	cv2.imshow('edges', edges_inv)
	
	cv2.imshow('laneview', lane_view_small)
	
	cv2.imshow('binlaneview', lane_view_binary_small)
	
	if cv2.waitKey(1) & 0xFF == ord('q'):
		break

cap.release()
cv2.destroyAllWindows()
