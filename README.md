# BalloonPop-Scala

A balloon popping Android game written (mostly) in Scala and work-in-progress. It uses blobengine
which is in the [AndroidGDX](https://github.com/adsgray/AndroidGDX) repository
and which is 100% Java.

It will be timed (how many balloons can you pop in 1 minute?) and kid-friendly
(read: easy).

### Sounds
* Some from [Universal Soundbank](http://eng.universal-soundbank.com/cartoons.htm)
* Some from [Flashkit](http://flashkit.com)

### Font
From [1001 Free Fonts](http://1001freefonts.com)


### TODO:
1. Tag blobengine when changes settle
2. ~~BlobengineUtil.scala to wrap triggers, sources, etc~~
3. ~~font~~
4. ~~sounds added to res/raw but not integrated~~ ~~vibrate~~
5. ~~time limited game with countdown~~
6. high scores (top 5, also top 5 score/taps ratio) Maybe use Google Play?
7. ~~Lock orientation at portrait~~
8. ~~sound/vibration settings screen~~
8. help screen
9. ~~must allow multiple simultaneous "live" Taps but requires Map-ping them~~

### Google Play TODO:
1. ~~Add BaseBameUtils to Eclipse~~
2. ~~Make MainActivity extend it~~
3. ~~Add _connect with Google Play_ button to Main layout~~
4. ~~Add connection complete handler which uploads locally stored
   scores/achievements to Google Play.~~

### Google Documentation Hilariousness
These two documents seem to disagree (_mSignedIn_ vs mGamesClient.isConnected):
* <https://developers.google.com/games/services/training/signin>
* <https://developers.google.com/games/services/android/init>
* P.S. the version of BaseGameActivity I have doesn't have _mSignedIn_. Also
  doesn't have _mGamesClient_. But has _getGamesClient()_
