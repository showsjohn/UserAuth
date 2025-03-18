 A simple demo of user registration/authentication with Spring Security and JWT

 To run, simply create a postgresql server and add the login details in the application.properties file

 Run the `UserAuthApplication` file. Next, open your browser and navigate to `localhost:8080/register`

 From here, you can register your user account. Upon completion, you should be redirected to the login page. If you successfully login, you should be redirected to the localhost:8080/login page

To test if the JWT token is correct, type `localhost:8080/login` in the browser. If you see `Success!` this means you authenticated via your token and dont require manually logging in.
