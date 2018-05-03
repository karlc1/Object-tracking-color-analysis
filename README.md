# Object-tracking-color-analysis

This project detects and logs the colors of users clothes. It uses a MOG background subtractor for isolating moving objects from a static camera feed to identify people, and uses K-nearest neighbour clustering on remaining pixels to extract the dominant color. The camera feed can be either a webcam or a remote video stream, tested with Axis Cameras. The final color values and time stamps for each frame are pushed to a server and used for visualization of color clothing trends on a web server. Neither the server or web service code is present in this repo. 
