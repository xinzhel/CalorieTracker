# CalorieTracker
This app (include server-side and client-side) aim towards building a personalised fitness application
that will keep track of what you eat, your daily calorie intake and calories burned, and provide you with useful
information and reports. It will also allow you to set goals and inform you every day if you have met your
goals. The final application will retrieve the information from public web APIs and the RESTful web service.

* Task 1 – Invoking public web APIs :
invoke and consume two public APIs:
 Google Custom Search to get an image of the food item and useful information
 The API provide calorie and nutrition information for food items and allow us making queries about the new food items that does not exist in your database(server).
The APIs’ web sites can be chosen:
https://developer.edamam.com/food-database-api-docs
http://ndb.nal.usda.gov/ndb/api/doc
http://platform.fatsecret.com/api/

Google
a) make queries to Google APIs to retrieve useful information about a food item (the new food items entered by the user).
b) also use the Google search to find and display at least one relevant image of the food item.

edamam APIs
c) make queries from the FatSecret or National Nutrient Database APIs using a keyword (a food item name).The details about the screens are provided under Task 6. (7 marks)

* Task 2 Android Client of RESTful WS:
a) The android application client will connect to the server and consume the RESTful web service created in Phase1. You need to make http connections using HttpURLConnection to invoke all the methods required for completing tasks in this assignment. (2.5 mark)
b) Accessing data and executing all the queries from the server side (and the web service) should be achieved using the AsyncTask approach. (2.5 marks)

* Task 3 Android SQLite Database and Services (10 marks):
In this application, in addition to the data that is stored on the server side and can be queried through the web service, I also store data about the user locally on the mobile phone in one table:
a) The daily steps table will store the steps entered by the user in a day at different times. This table will be queried later to provide a total number of steps per day.
b) Your code logic should make sure at the end of the day (11:59 pm), the following data is written (added) to the backend database (to the report table) of RESTful web services (by calling the POST method). You will use Android Services to achieve this. The POST method will add a new record to the report table at the end of the day with the following information:
o The user calorie set goal (this will be entered daily by the user in the home screen)
o the total calories consumed for that day (based on REST calculation methods),
o the total number of steps (based on the daily steps table’s data)
o the total calories burned for that day (based on REST calculation methods)
c) Then I delete all the existing data entries in the SQLite table. This means the table always stores the data for the current day. 

* Task 4 Login and Sign up Screen :

* Task 5 Home Screen - Dashboard (10 marks):
a) The home screen – this page should display “Calorie Tracker” as the app title, a picture related to fitness, and the current date and time. The main page should also welcome the user by their first name.

b) This screen enables the user to enter a calorie goal for that day (facilitate data entry and avoid incorrect data entry by using an appropriate UI input control). If the user has already set the goal, this screen should allow the user to edit the goal. (2 marks)
c) The main page will use navigation drawer and fragments to navigate to other screens. 

* Task 6 My Daily Diet Screen:
a) My Daily Diet screen will have two lists. The first list will allow the user to select a food category such as Drink, Meal, Meat, Snack, Bread, Cake, Fruit, Vegies, and Other. The second list will be populated automatically, from the food table in the server-side database by invoking REST methods, with the food items which are under the selected category. 
b) The screen should also allow the user to enter a new food item that doesn’t exist in the second list. This new food item will be added to Food table using the POST method. 
The details of the new food item (i.e. calorie and fat amount) will be retrieved from public APIs (FatSecret or National Nutrient Database) and will be displayed. The selected food item’s picture and other related information will be displayed by invoking Google. (APIs’ marks already allocated in Task 1)

* Task 7 Steps Screen :
a) The Steps screen: this screen enables the user to enter the steps they have taken several times a day (you need to facilitate data entry and avoid incorrect data entry by using an appropriate UI input control). The screen will allow displaying all the entries and their time of entry. The data can be edited. 
The number of the steps entered each time during the day will be stored locally (in the SQLite database), and at the end of day, the total number of all steps will be added to the server-side table as described in Task 3.

* Task 8 Calorie Tracker Screen:
a) The Calorie Tracker screen will show the set goal (already entered by the user), the total steps taken, and the total number of consumed and burned calories for that day up to that time. To calculate the consumed and burned calories you need to invoke REST calculation methods using the total steps taken, all the food eaten, and other related information. The total number of steps plus calories burned while at rest will be used to calculate calories burned for a user through invoking the REST calculation method for steps. (Note: You have the option of including the contents of this screen and/or the steps screen into the home/dashboard screen)

* Task 9 Report Screen:
The Report screen: this screen will enable the user to select and generate two types of reports. Our assumption here is that the user will use the Calorie Tracker Screen to view every day data, and use the reports for the past/historical data.
a) Pie chart: this section/screen will include a date picker to allow the user to select a date. Then the user’s fitness report is displayed using a pie chart that shows the total calories consumed, the total calories burned (including calories burned at rest and for steps taken), and the remaining calorie (after compared to the calorie set goal). This will require querying the report table calling the right REST method. The labels and percentages should be shown on the pie chart.
b) Bar graph: a bar graph will show the total calories consumed, and the total calories burned (at rest and for steps taken) per day for a certain period. The screen will allow the user to enter a starting date and an ending date to create the report for that period of time. In the bar graph, you need to differentiate between calories consumed and burned (using different colours). If you use a line graph for this task, you could achieve only the half of the mark.

* Task 10 Map Screen :
a) The map screen will show the user’s home location. You need to programmatically convert the location of the user based on their address and postcode into latitude and longitude values (using either Google Geocoding API or Android built-in libraries like Geocoder). Then use the latitude and longitude values for displaying the location on the map. 
b) The map screen will also show the nearest parks to the user within the radius of 5 kilometres.
c) When the user taps on a park marker on the map, a new screen/view or widget should appear with the park information, e.g. the name. 
d) The marker for the user and the marks showing nearest parks should have a different colour.
