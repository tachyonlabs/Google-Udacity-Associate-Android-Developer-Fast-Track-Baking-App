# Baking App
When completed, this will be my submission for the third project in the Google/Udacity "Associate Android Developer Fast Track" course.

![A screenshot of my Baking Time App](https://raw.githubusercontent.com/tachyonlabs/Google-Udacity-Associate-Android-Developer-Fast-Track-Baking-App/master/baking-time-screenshot.png "A screenshot of my Baking Time App")

![A screenshot of a recipe detail on my Baking Time App](https://raw.githubusercontent.com/tachyonlabs/Google-Udacity-Associate-Android-Developer-Fast-Track-Baking-App/master/baking-time-recipe-detail-screenshot.png "A screenshot of a recipe detail on my Baking Time App")

![A screenshot of a recipe step detail on my Baking Time App](https://raw.githubusercontent.com/tachyonlabs/Google-Udacity-Associate-Android-Developer-Fast-Track-Baking-App/master/baking-time-recipe-step-detail-screenshot.png "A screenshot of a recipe step detail on my Baking Time App")

### General App Usage
Meets Specifications:

* [x] App should display recipes from provided network resource.
* [x] App should allow navigation between individual recipes and recipe recipeSteps.
* [x] App uses RecyclerView and can handle recipe recipeSteps that include videos or images.
* [ ] App conforms to common standards found in the [Android Nanodegree General Project Guidelines](http://udacity.github.io/android-nanodegree-guidelines/core.html).

### Components and Libraries
Meets Specifications:

* [x] Application uses Master Detail Flow to display recipe recipeSteps and navigation between them.
* [x] Application uses Exoplayer to display videos.
* [x] Application properly initializes and releases video assets when appropriate.
* [ ] Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
* [ ] Application makes use of Espresso to test aspects of the UI.
* [x] Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with ContentProviders if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

### Homescreen Widget
Meets Specifications:

* [ ] Application has a companion homescreen widget.
* [ ] Widget displays name list for desired recipe.

### Third-party libraries used

* [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

### Photos used under Creative Commons license

The JSON for the recipes contains URLs for videos illustrating some of the steps, but no links to photos, and no recipe descriptions. I would say that in "real life" no one who went to all the trouble to make multiple videos for each of their recipes would neglect the very basic steps of taking at least one photograph and writing a short blurb/tagline for each of the finished products, so to make this app look more like an actual product as well, I wrote some blurbs, and am using the following Flickr photos, which the photographers have made available for use under the Creative Commons license:

* For Nutella Pie: "[I invented this](https://www.flickr.com/photos/leedav/4328677446/)", by [Lee Davenport](https://www.flickr.com/people/leedav/)
* For Brownies: "[Chocolate-Mint Brownies](https://www.flickr.com/photos/theryn/5727350257/)", by [Theryn Fleming](https://www.flickr.com/people/theryn/)
* For Yellow Cake: "[yellow cake](https://www.flickr.com/photos/stuart_spivack/2584637478/)", by [Stuart Spivack](https://www.flickr.com/people/stuart_spivack/)
* For Cheesecake: "[Cheesecake Supreme](https://www.flickr.com/photos/cuttingboard/2699220126)", by [Emily Carlin](https://www.flickr.com/people/cuttingboard/)

### Notes
