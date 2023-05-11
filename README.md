# NasaApp
Search for images related to space has become easier. This application will allow you to:
* Search any pictures using NASA database
* Save your favorite pictures

### Screens Preview
![](https://i.yapx.cc/WBc7g.png)
![](https://i.yapx.cc/WBc7i.png)
![](https://i.yapx.cc/WBc7j.png)

### Description
The application implements user login and registration through its own server. Confirmation of the phone number through Firebase Authentication. Images are received through the free NASA API.

### Architecture
* The principles of **Clean Architecture** were used in the development
* **MVI** architecture was chosen as a template for the **UI** layer

### Technology Stack
- DI: Dagger
- Room
- Coroutines/Flow
- Retrofit
- Firebase

### Project Build
**An api key is required** for the application to work correctly. You can get key **for free** [at the link.](https://api.nasa.gov/)
Then add line with key to `local.properties` file. Example:
```XML
// Top level local.properties file

NASA_API_KEY = your_api_key
```

For correct registration, **integration with firebase is required**. To do this, you need to create and configure a project [on the firebase.](https://console.firebase.google.com/) And then add the config file `google-services.json` to into your module (app-level) root directory.
