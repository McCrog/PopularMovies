# Popular Movies

This is the repository for the movie app that build during the [Developing Android Apps](https://www.udacity.com/course/new-android-fundamentals--ud851) course at Udacity.

To use the app, you need create 'movie_api.properties' outside the project directory. Then, in the properties file, you must specify an API_KEY:

apiKey=YOUR_API_KEY

(Not recommended) You can specify an API_KEY directly in '/app/build.gradle':

buildConfigField('String', 'API_KEY', '"YOUR_API_KEY"')