# YouTube audio downloader (CLI)

A Spring Boot command-line application that downloads audio files from a YouTube playlist URL. The app converts each
video to an audio file using an external API and saves files to your local filesystem.

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

### Features
- Concurrent conversion and download of videos, significantly speeding up processing time, ideal for large playlists.