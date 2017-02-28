# MemoryGame

Creating a memory game 
* Load the 9 images downloaded from flickr’s api in a grid 
* Users are given 15 seconds to remember the images
* Once 15 seconds pass by, the images are flipped over
* The user is then presented with one of the 9 images, and is asked to point out its location.
* If user correct identifies, image is flipped back and a sound is played otherwise a toast is displaying asking him to try again.
* The current turn is not complete until the user identifies the correct image
* The game ends when all 9 images have been flipped back
* All images are fetched from Flickr’s public api : http://www.flickr.com/services/feeds/docs/photos_public/ 
