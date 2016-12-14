# MobileDevicesProject
A Repository for CSCI 4100U; Mobile Devices final project.

Team:
- Mohamad Vedut, theflash888 100535400 <br>
- Devin Westby, Nephna, 100522109 <br>
- Dominick Mancini,Rockwell95 100517944 <br>


**Objective of Project:** The purpo#se of this project was to develop a solution which combines the tools and concepts used in the CSCI 4100U class which include: android layouts, resources, intents, broadcast receivers, internet resources and database storage/access and the use of geocoding and Google Maps.

**Project:** The goals of this project was to develop an application that empowered everyday travellors to reach one destination to another in the most optimal way possible. The application leverages the Google Maps API to create an environmet where the user specifies where they want to go - from beginning of the journey to end, the budget they have to travel there and and the vehicle they are using to travel with. The application uses the current gas pricing, vehicle fuel consumption and provides the best 4 options (if all available) to reach a particular destination. The Algorithm developed involves looking at all the current options and formulating the top 4 for the user to reach their destination without going over budget. 

The application also enables the user to look at the 5-day forecast from the webview of a website which enbales them to make arrangements ahead of time as well as viewing gas pricing specifically by being referred to GasBuddy website directly to be more speicific of the gas prices in the area they are looking into. 

In order to use the application, the user musts sign-up with their own credentials and create their own username and password to have access to the application at all times. 

**Instructions**
1. Run Application <br>
2. Signup as a user <br>
3. Login to the application <br>
4. Hit the blue direction button on top right corner <br>
5. Enter destination and vehicle being used, then "Get Direction" <br>
6. View Map information <br>
7. Select the different routes inside Spinner <br>
8. Other functions: -open other 2 buttons for weather forecast or gas information (Webview and URL)

**Walkthrough**:

<img src="screenshots/Screenshot_2016-11-27-15-27-04.png" width="500" height="800">
<br>
**Figure 1:** If the user is not already, registered in the application, they must register their informatio by clicking Sign-Up at the bottom of the page.

<img src="screenshots/Screenshot_2016-11-27-15-27-08.png" width="500" height="800">
<br>
**Figure 2:** The user will input all the necessary fields inside the application to sign-up.

<img src="screenshots/Screenshot_2016-11-27-15-27-34.png" width="500" height="800">
<br>
**Figure 3:** Once the user logs in, they will be taken to the main Map on the page. If they would like to plan a trip they will simply click the top right button on the Map screen.

<img src="screenshots/Screenshot_2016-11-27-15-28-09.png" width="500" height="800">
<br>
**Figure 4:** The user will then be asked to input the start and end-point of the destination which calls the Google API to allow the user to search worldwide. 

<img src="screenshots/Screenshot_2016-11-27-15-28-26.png" width="500" height="800">
<br>
**Figure 5:** The user will then enter the destination along with the type of vehicle they drive. This information is stored inside the database which was collected on car dealership sites. 

<img src="screenshots/Screenshot_2016-11-27-15-28-33.png" width="500" height="800">
<br>
**Figure 6:** The application will automatically pull the average gas price from GasBuddy and use that along with Figure 5 to calculate the optimal destination. Then hit "Get Directions".

<img src="screenshots/Screenshot_2016-11-27-15-28-33.png" width="500" height="800">
<br>
**Figure 7:** The user will be taken to the map to view the different options inside a spinner. 

<img src="screenshots/Screenshot_2016-11-27-15-28-46.png" width="500" height="800">
<br>
**Figure 8:** The options available are set from most optimal to least optimal, but the options presented the top 4 (at most.)

<img src="screenshots/Screenshot_2016-11-27-15-28-56.png" width="500" height="800">
<br>
**Figure 9:** The user will select one and the map will display the information accordingly. 

<img src="screenshots/Screenshot_2016-11-27-15-29-16.png" width="500" height="800">
<br>
**Figure 10:** If the user wishes, they may also get the Gas Price manually from GasBuddy website. The button would automatically take them to the URL. 

<img src="screenshots/Screenshot_2016-11-27-15-29-24.png" width="500" height="800">
<br>
**Figure 11:** The user may also be taken to a webview inside the application which would open up a 5 day forecast of the weather. They are also able to interact with the webview as they would inside any browser. 

# Initial Design Implementations

<img src="screenshots/iPhone2.png">
<br>

**Figure 12:** Initial implementation concept prior to core android development. UXDesign lead by: Mohamad Vedut.
