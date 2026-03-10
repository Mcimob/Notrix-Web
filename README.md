# Notrix Web

Notrix Web serves to visualize relationships between solutions to kaggle competitions. Also you can upload your own solution to a competition or whole solution sets for the application to analyze.

The application is amde of two distinct parts:
- Notrix-Web: A Vaadin application that is the frontend and backend to the web application
- Server: A python FastAPI server that processes uploaded competitions and notebooks

## Starting in Development Mode

To start in development mode you need a postgres database. This is probably easiest to start inside a docker container with its port exposed, or as a service on your local machine. Once you have that database, you can adjust the database parameters in the `application-dev.properties` file.

After this you can start the application using `./mvnw` from inside the `java` directory. Alternatively you can start the application in debug mode from inside IntelliJ (which I recommend for development), as this gives you the ability to hot-reaload the application.

Lastly you have to start the FastAPI server. For this, first create a python environment of your choice (I recommend using venv like `python3 -m venv .venv`) inside the `python` directory. After activating the environment (`source .venv/bin/activate`) install the dependencies using `pip install -r requirements.txt`. This might take a while, as the required packages are wuite large. Once everything is successfully installed, you can run the FastAPI server in development mode for hot-reloading using `fastapi dev app/server.py`. This will host the application on port 8000 by default. If you change this, make sure to also change the `application-dev.properties`.
Make sure to either set the "OPENAI_API_KEY" environment variable or put a .env file in the `app` directory for the GPT analysis to work.

## Building for Production

To deploy this application, you can simply use the provided `docker-compose.yml` file. Make sure to add
