# YouTube audio downloader (CLI)
A Spring Boot command-line application that downloads audio files from a YouTube playlist URL. The app converts each
video to an audio file using an external API and saves files to your local filesystem.

### Features
- Concurrent conversion and download of videos, significantly speeding up processing time, ideal for large playlists.

### Prerequisites
- Java 25
- Google OAuth 2.0 credentials
    - Refer to [Using OAuth 2.0 to Access Google APIs](https://developers.google.com/identity/protocols/oauth2)
    - Once you obtain your own set of credentials, download the file and rename it to ```client_secret.json```
    - Place the file in ```src/main/resources```

### Build and run the application
- ```git clone```
- ```cd``` into the project folder
- ```./mvnw clean package```
- ```java -jar target/yt-audio-downloader-0.0.1-SNAPSHOT.jar```

### Configuration options
Configuration file ```src/main/resources/application.yml```
- **Required**
    - ```app.config.playlist``` YouTube playlist URL
- **Optional**
    - ```app.config.output``` Output directory, where downloaded files will be saved
        - default: ```download```
    - ```app.config.remove-after-download``` Whether to remove downloaded videos from the playlist. Only successfully
      downloaded videos will be removed.
        - default: ```false```

Configuration values defined in ```application.yml``` can be overridden by providing arguments directly to the
application using option flags (e.g. `````--app.config.playlist=<PLAYLIST-URL>`````).
