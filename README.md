# Sandbox-1

What will this become? Some sort of a game?
![game 1](https://user-images.githubusercontent.com/4059636/214014215-49bba214-b858-4bc0-988a-a059886dbc44.gif)

Read on below for project specifics.

1. [Game Design Document](#game-design-document-sparkling_heart)
2. [Credits](#credits)
3. [Project Comments](#project-comments)
4. [Other](#other)
5. [Project Kanban Board](https://github.com/users/Slideshow776/projects/2/views/1)

## Game Design Document :sparkling_heart:

1. :blue_heart: Overall Vision

   - **Write a short paragraph explaining the game:**

     You play as a child catching fish with their ice skates. In order to catch the fish you have to encircle them, doing so be careful not to fall into the water.

   - **Describe the genre:**
     This is a casual action drawing game.
   - **What is the target audience?**

     Adults and children.

     The average player is assumed to be casually playing for a couple of minutes.

   - **Why play this game?**

     The challenge presented is to encircle the fish with the mouse cursor or finger if on an Android device.  
      The avatar will move after touch release, and it's important not to draw your shapes such that the avatar does not fall into the water, and successfully encircles the moving fish.

     The game is also ment to bring forth positive feelings regarding the need for inclusion of different people, as the main character is [Sami](https://en.wikipedia.org/wiki/Sámi).

2. :purple_heart: Mechanics: the rules of the game world

   - **What are the character's goals?**

     To catch all the fish on the level.

   - **What abilities does the character have?**

     The character does not have any abilities, other than that the player needs to draw the correct shapes at the correct time.

   - **What obstacles or difficulties will the character face?**

     Drawing overlapping shapes will result in the player falling into the water, and thus loosing the game.
     The player also needs to time their avatar so the shape drawn includes moving fish.
     The player will also need to avoid impassable terrain.

   - **What items can the character obtain**
     Fish, which is under the ice. Draw shapes around them to catch them.
   - **What resources must be managed?**
     Stamina, which is drained when drawing shapes.

3. :heart: Dynamics: the interaction between the player and the game mechanics

   - **What hardware is required by the game?**

     - Desktop needs to have a functional mouse, and screen. This game will not require a powerful computer.

     - Android devices needs to have a funcitonal touch technology or mouse option

   - **What type of proficiency will the player need to develop to become proficient at the game?**

     Physical Coordination Challenges:

     - Speed and reaction time
     - Accuracy and Precision
     - Timing

   - **What gameplay data is displayed during the game?**

     - A stamina bar, which drains down to zero when moving.
     - A label displaying how many fish are left.

   - **What menus, screens, or overlays will there be?**

     - A splash screen, displaying the game developer logo
     - A main menu screen
     - An options screen
     - A level select screen
     - Level screens

     The level screens will have overlays with their, respective options, for game start, over and end.

   - **How does the player interact with the game at the software level?**

    With their mouse or touch controller. For desktop there might be hotkey shortcuts.

4. :green_heart: Aesthetics: the visual, audio, narrative, and psychological aspects of the game

   - **Describe the style and feel of the game.**

        Slow pace turn based, plenty of time to think.
        Vibrant cartoony colors and shapes.
        Feelings of being out in nature.        

   - **Does the game use pixel art, line art, or realistic graphics?**

        Pixel art.

   - **What style of background music, ambient sounds will the game use?**

        * Calm music, possibly something reminicent of Sami culture?
        * Nature ambiance sounds, birds chirping, wind, reindeers, ...

   - **What is the relevant backstory for the game?**

        The child protagonist, Elle, are wanting to go with the adults hunting for food. They are however denied participation because they are too young to join.
        Elle then finds another way, fishing in the nearby frozen lake using their skates.

   - **What emotional state(s) does the game try to provoke?**

        The game centers around the feelings of pride and engagement.
        It also explores feelings of shame in disempowerment, and frustration of being excluded.

   - **What makes the game fun?**
    
        Figuring out how to catch fish, and avoid falling into the water.

5. :yellow_heart: Development

   - **List the team members and their roles, responsibilities, and skills.**

     This project will be completed individually; graphics and audio will be obtained from third-party websites that make their assets available under the Creative Commons license, and so the main task will be programming and creating some graphics.

   - **What equipment is needed for this project?**

     A computer (with keyboard, mouse, and speakers) and internet access will be necessary to complete this project.

   - **What are the tasks that need to be accomplished to create this game?**

     This project will use a simple Kanban board hosted on the project's GitHub page.
     The main sequence of steps to complete this project is as follows:

     - Setting up a project scaffold
     - **Programming game mechanics and UI**
     - **Creating and obtaining graphical assets**
     - Obtaining audio assets
     - Controller support
     - **Polishing**
     - Deployment

   - **What points in the development process are suitable for playtesting?**

     The main points for playtesting are when the basic game mechanics of the level screen are implemented, and when it is visualised. The questions that will be asked are:

     - Is the gameplay and UI understandable?
     - Is the gameplay interesting?
     - How do the controls feel?
     - How is the pace of the game?
     - Are there any improvement suggestions?

   - **What are the plans for publication?**

        The game will be published on Android Play Store for a price.
        It will be featured on Sandra's website and github.

## Credits

## Project comments

### Triangulation
* Using [LibGDX's ear clip algorithm](https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/EarClippingTriangulator.java).
* [Ear Clipping explained](https://www.youtube.com/watch?v=QAdfkylpYwc).

### Masking
* Libgdx's documentation on masking, and an [explanatory video](https://www.youtube.com/watch?v=qDKmcNFFFng&t=613s).

* Implemented in [this commit](https://github.com/Slideshow776/Sandbox-1/commit/4fe9886aa040796c5cb44c872b11f8b9c6c48b30).

## Other

For other project specifics check out the [commits](https://github.com/Slideshow776/Sandbox-1/commits/main).

[Go back to the top](https://github.com/Slideshow776/Sandbox-1).
