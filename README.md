Cheap Chomp<br>
by Camille Christie, Tommy Ho, and Michelle Sun<br>

Our app is a grocery expense tracker designed to help users manage their grocery budgets and locate stores nearby. It features user login authentication, allowing individuals to securely track expenses, find the best prices for groceries in their area, and make smarter budgeting decisions.

Functionality<br>
-Our app uses the Firebase login/register and Google Authentication to create personal accounts for users.
-Once users are logged in permissions are then requested from the user to obtain their current location and find the nearest Kroger Store in their area.<br>
-Once the location for the Kroger store is obtained we can then query Kroger API for grocery items, which we can add to our own personal grocery list.<br>
-Users will then have to visit the grocery store in person to purchase their groceries, but will be able to checkoff the items in the grocery list.<br>
-Checked off items can then be added to the total monthly grocery expense and viewed as a bar graph on our statistics page, where users can compare their monthly expenses on groceries vs. previous months.<br>

Technical Implementation<br>
-Firebase was used to create the login and register pages.<br>
-The Google Play Services Location Library was then called in order to obtain the current location i.e the longitude and latitude and pass them to the other screens using the ViewModel.<br>
-On our KrogerProductScreen we use the Kroger Location API to retrieve the closest store's id and called the KrogerProductAPI on that storeid to retrieve a list of items with the selected filters applied.<br>
-Selected items from the Product List will then be stored in the Firebase Database, if connected to the Internet, otherwise it is added to the Room offline database.<br>
  -When reconnected back to the Internet the Room database would use the Work Manager to sync back the online database with the offline database.<br>
-Statistics i.e the graph are then created using #implementation ("com.github.tehras:charts:0.2.4-alpha") to showcase the total monthly expenses in comparison to previous months.
-User friendly toggles such as navbars, swipes, and event triggered buttons to create a more interactive UI.<br>

Challenges<br>
-Initially we planned to use the Instacart API and a different github library to accomplish our desired functions, however since they could not be implemented due to unauthorized access, we had to pivot to using the KrogerAPI as our new grocery price locator and #implementation ("com.github.tehras:charts:0.2.4-alpha") to plot our statistics.<br>
-We had a lot of difficulty:<br>
  -Working with Databases<br>
  -Understanding/Implmenting DataFlow within the MVVM/MVC infrastructure<br>
  -Researching possible APIs for Grocery Prices<br>
  -Implementing Tests<br>
  -Everything basically required a lot of time and research, which was morally and physically draining<br>
    -On the bright side our UI has never looked better than it does right now.<br>

Modified Features<br>
-We are no longer doing an input form for other types of expenses since the professor suggested we focus solely on grocery expenses.<br>
-The statistics now compares the previous months grocery expenses for the year.<br>

If you have problems running this application, please include your own (CLIENT_ID, CLIENT_SECRET) key pair from Kroger Api and API_KEY from Google Api. The (CLIENT_ID, CLIENT_SECRET) key pair should be add to the local.properties file within Gradle before preforming the commands Clean Project, and Rebuild Project.<br>

In order to access the Google Authentication login you may need to generate a SHA-1 key (API_KEY) by running ./gradlew signingReport in the Android Studio terminal, then add it to the google firebase project settings and replace the google-services.json file with the new one.
