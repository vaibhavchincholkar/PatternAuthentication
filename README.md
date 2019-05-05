# Pattern Authentication Mechanism for Augmented Reality

PatternAuth application makes use of ARcore library for augmented Reality.
PatternAuth can be used to authenticate users based on their pattern.
It can be seen as an extension of the pattern authentication we see on Android phones.
In PatternAuth user has to create a pattern with the help of ARobjects on the surface. User can tap on the screen to 
put objects on the surface. PatternAuth accepts user pattern with some degree of error as producing exact pattern is very difficult.

After the user is authenticated, the user is redirected to ARBALLMASTER game. link to ARBALLMASTER :

[https://play.google.com/store/apps/details?id=test.vee.com.arballmaster&hl=en_US](https://play.google.com/store/apps/details?id=test.vee.com.arballmaster&hl=en_US )

(Here we are not going to talk about ARBALLMASTER game)

##### App activity interaction can be seen in the following diagram.

![Alt text](mockup.JPG?raw=true "Title")

### Flow
1. On login Activity user can click on Sign up or Login button.
2. On Sign up click user will be asked to enter the Player Name.
3. On Confirm click user can create his/her pattern on the surface.
4. On submit user will be redirected to an activity where he/she can test the pattern they submited.
   Once player get enough confidence on the submitted pattern they can store the pattern.
5. On login Click player will be asked to enter the Player Name.
6. On confirm click player will be asked to enter the pattern for provided Player Name.
7. If pattern is correct the user will be redirected to the ARBALLMASTER game.

### How pattern is recognized

Consider the following pattern 
![Alt text](p1.JPG?raw=true "Title")
In this Distance between objects are shown. 
And each object has a x coordinate and a Y coordinate, here we are considering the order A-B-C.

The distances between objects are calculated using simple Euclidean distance formula.
```
distance =sqrt {(X1-Y1)^2+(X2-Y2)^2}
```

When the user puts the objects on the surface, the system calculates the distance between every object to every other object.
In this case, the system will store distances between
A to B, A to C, B to A, B to C, C to A, C to B.


Now when a user puts out the pattern again while logging-in, the system compare the distances between provided objects and distances between stored objects. As we can see producing exact same pattern again is very difficult for humans that's why we allow the user to enter the pattern with some degree of inaccuracy of error. (The error value is defined in the system using trial and error)

consider the following pattern given by the user.

![Alt text](p2.JPG?raw=true "Title")

Even though distances between the objects are same the order is not, here user put down objects in order B-A-C.
In this case, the system will compare distances between:

A to B vs B to A

A to C vs B to C > *The distance error between this comparision will be much higher than error value and pattern will not be matched and system will not authenticate the user.* 

So we just do not want user to provide correct pattern but also correct order.
### Usage

- Clone this repo
- Open in Android Studio
- Add Google sceneform plugin to android studio
- Compile and run on ARcore enabled device.
- \o/
