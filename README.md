# Space Seal

<h2> About </h2>
  Application created as a part of my engineering project. It let's user to record not only a video, but also space orientation for every frame, transcode selected video into .mkv and even AV1 (both not supported by android framework). Nextly video may be streammed using HLS or RTSP protocol
  
  <br>Second part of engineer work (desktop server): https://github.com/Swistusmen/gCosm</br>
<h2> Tech Stack </h2>
  <br> Java, Kotlin </br>
  C
  <br>GStreamer</br>
  ffmpeg
<h2> Functionalities </h2>

<h3> Main </h3>

[<img src="https://github.com/Swistusmen/AndroidGstreamer-template/blob/master/Screenshots/main.png" width="400"/>](image.png)

<h3> Recording Activity </h3>

[<img src="https://github.com/Swistusmen/AndroidGstreamer-template/blob/master/Screenshots/recording.png" width="400"/>](image.png)

  <br>-Let member record a video- frame rate is set to 30. Data are save in External video storege</br>
  <br>-Switcher on, makes app save position coordinates. Data are collected from sensors at the same time as frame. Data are saved as .txt file, with the same name and location as recordered video</br>

<h3> Transcoding Activity </h3>

[<img src="https://github.com/Swistusmen/AndroidGstreamer-template/blob/master/Screenshots/Streamming.png" width="400"/>](image.png)

  <br> -Let member to select video- also not recorded by this app </br>
  <br> -Let member to change container files .mp4 <-> .mkv </br>
  <br> -Let member to change video formats .h264 <-> .av1 </br>
  <br> -After operation ends, application informs member whether it ended successfully or not, and time which it took </br>
  <br> -New video has the same name as the origin, what may cause troubles </br>

<h3> Streamming Activity </h3>

[<img src="https://github.com/Swistusmen/AndroidGstreamer-template/blob/master/Screenshots/Screenshot_20211118-150154.png" width="400"/>](image.png)

  <br> -Let member to select video to stream </br>
  <br> -Let member to select stream protocol and video format. For HLS app demand external HTTP server (you can easily download it from google play store </br>
  
 <h2> Technical notes </h2>
 <br> HLS demand external http server </h2>
 <br> To build an app yourself, I recommend you to follow official Gstreamer documentation: https://gstreamer.freedesktop.org/documentation/installing/for-android-development.html?gi-language=c
 
 <h2> Performance </h2>
 <br> Application has been tested on Xiaomi Mi A2 with android 10. Transcoding video (3 seconds, 30 fps) from MP4(H264, AAC) into MKV(AV1, AAC) took 6 minutes. Purpose of this app is to test oportunities to develop applications with a support of AV1. What is interesting however, is that plyaing such a video was not a problem. Conclusion is,you an easily watch streams in AV1, but live streamming is rather hard </br>
