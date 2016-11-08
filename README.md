# The Explorer
Codepath Group Project

Members: Aditi.K ,Huyen.T, Raj Praveen

## Description
The Explorer is your travel buddy. It stays with you throughout your journey. It lets you look for places, plan your trip, post pictures and add reviews/ratings. It lets you create a story out of your journey which you can share with your friends and family and cherish those moments.

## User Stories

### Required
- User can *login* with Facebook or Google
- User can see the *home screen* that includes:
    - *User details*: 
      - User name and profile image
      - A placeholder for profile image prepopulated from Facebook or Google 
      - A button inside placeholder to upload a new profile photo
      - A placeholder for cover photo prepopulated with a default image
      - A button inside placeholder to upload a new cover photo
    - *User timeline*:
      - Scrollable timeline of all the user's "stories" and "trips" (i.e. past stories, current story if any, and future trips)
      - Each story/trip has a title, cover photo and travel date
      - Tapping on a story/trip takes the user to a separate "Story" view
      - Button to create a new story/trip (could be toolbar button or floating action button)    
- User can edit the story/trip in the timeline
    - Delete the story
    - Share the story with friends
    - Edit icon which takes the user to Story view.
- User can create a *New Trip*. The screen includes:
    - A search bar with autocomplete feature. (Using https://developers.google.com/places/android-api/autocomplete)
    - A grid view with possible travel destinations (i.e. top 10 travel destinations) or view for the searched destination.
    - Tapping on a destination takes the user to separate "destination" view
- User can view details of *Destination* which includes:
    - Button to choose this destination for the new trip(could be a tick on top-right of screen)
    - "Top 10" in categories for that destination (i.e.Categories- Things to Do, Places to Eat, Sights to See)
    - Each category can be scrolled horizontally
    - Each item within the category has a title and respective image  
- User can tap on item to see *Details of item* which includes:
    - Name
    - Description
    - Address
    - At least one representative photo
    - Ratings bar
    - Button to add the item to their current trip.
- User can view/edit the trip details in *Story view* which includes:
    - Name of destination and travel date
    - Items added for the destination(i.e. Things to Do, Places to Eat, Sights to See, if any)
    - Horizontal scroll view of photos uploaded by user(if any)
    - Notes on journey experience
    - Ratings bar to rate the destination
    - Button to upload new photos
    - Button to save the story

###Optional
- User can tap on an item’s “favorite” icon (i.e. heart or star) to save that item to their favorites list of story/trip
- User can see more info about the destination( website, days/hours of operation, location on maps, reviews, etc)
- User can drag to reorder items on users timeline
- User can have shortcut to annotate an item by swiping right to reveal available action icons:
    - Camera: adding photos from library or taking new photos
    - Video: adding videos from library or taking new videos
    - Text: adding text blurbs about their experience
- User can tap on a map icon to see a map view with the places visited
- User can add/edit/view reviews for an item(i.e. Things to Do, Places to Eat, Sights to See) of the destination

### Bonus
- User can tag people in their story/trip with whom they took the trip
  - This automatically shares the trip with them on their timeline
- User can view his friends trips and copy it to create a new trip
