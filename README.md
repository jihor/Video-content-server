# Video content server

System must have ffmpeg and MP4Box (version GPAC 0.5.2 or newer) installed
- avconv
`sudo apt-get install libav-tools`
- MP4Box
`sudo apt-get install gpac`

## Run example
1. ./gradlew bootRun
2. http://localhost:8080
3. Upload `clip.mp4` or `star_trails.mp4` from `src/main/resources/samples`. Files will be converted in `/tmp`
4. Play the video

OR

Play regular MP4 content from classpath (`clip` or `star_trails`)

## Encoding commands
1. `ffmpeg -i clip.mp4 -s hd1080 -c:v libx264 -crf 23 -strict -2 clip_1080p.mp4`
or
`avconv -i clip.mp4 -s hd1080 -c:v libx264 -crf 23 -strict -2 clip_1080p.mp4`
2. `MP4Box -dash 8000 -frag 8000 -rap -url-template -segment-name '$RepresentationID$/segment_' star_trails_1080p.mp4#video:id=1080p star_trails_1080p.mp4#audio:id=audio`   
`MP4Box -dash 8000 -frag 8000 -rap -segment-name '$RepresentationID$/segment_' with_sound_1080p.mp4#video:id=1080p with_sound_720p.mp4#video:id=720p with_sound_1080p.mp4#audio:id=audio`