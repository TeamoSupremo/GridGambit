
# Week 1
### 22/04
- Initial project created

[Nathan]
- Added XML with sizing and constraints for main menu buttons

[Josh]
- Added XML layout for achievements, level selector and information screens

### 24/04
[Nathan]
- Added Navigation/Intents for achievement, gamescreen and level selection activities, added player and achievement class

### 25/04
[Josh]
- Added achievements json file, added level class

# Week 2
### 27/04
[Nathan]
- Added XML for GameScreen

[Josh]
- Added XML for endless mode, added navigation/intent for endless mode

### 30/04
[Nathan]
- Added class for grid items 'GridTextView'
- Added 4x4 hardcoded grid items
- added xml drawable for grid layout

### 1/05
[Josh]
- Updated Colour palette
- Overhauled layouts with correct colours
- Added drawables for sound and info
- Updated landing page with neater menus

[Nathan]
- Added touch event listeners
- Dragging a block onto another will now sum them

### 2/05
[Nathan]
- Matching now works for 2 blocks with an absolute difference of 1

# Week 3
### 4/05
[Josh]
- Mathed out levels as discussed
- Added levels.json
- Working on achievements

### 5/05
[Nathan]
- Added DataManager class
- Added loading of levels from json file
- Changed creation of grid items to use numbers from json file

### 6/05
[Josh]
- Created level menu from levels.json

# Week 4
### 10/05
[Nathan]
- Added function to GridUtil for updating UI
- Made game UI update after each turn (score, turns left, etc)
- Changed main menu screen to dark

### 11/05
[Josh]
- Added check to see if grid has moves
- Added check for losing
- Added options for replaying same level or returning to main menu

### 12/05
[Josh]
- Added check for level complete

### 13/05
[Nathan]
- Added powerup resources
- Charge bar now filled based on score at 4:1 rate

### 14/05
[Josh]
- Updated achievement.json with actual achievements
- Made achievement screen load achievements from json and display
- Updated level button on main screen to show current player level
- Changed fonts for game squares and achievements menu

### 16/05
[Nathan]
- Added icons for charge bar
- Icons change on pressing charge bar
- Charge bar now usable at full charge
- Snackbar displays message on activation

# Week 5
### 19/05
[Josh]
- Added SoundManager Class
- Added onclick to sound button
- Added drawable for muted sound
- Added achievement check to GridUtil

### 20/05
[Nathan]
- Added method for updating colour of grid items
- Added drawables for: grid item shape, good match overlay, bad match overlay
- Grid items will now change colour after each successful match
- Grid items now have green border when hovering over matchable item, or red if not

### 22/05
[Josh]
- Added sounds
- Attached sounds to most game actions

[Nathan]
- saved progress now loads on opening the app

# Week 6
### 26/05
[Josh]
- Fixed some commenting
- Tidied up code

### 27/05
[Nathan]
- GameScreen code reformatting
- GameScreen code commenting

# Week 7
### 1/06
[Josh]
- Removed last 5 levels and corresponding achievements
- Achievements check implemented and working
- Added credits for images and sounds

### 3/06
[Nathan]
- Added more commenting to gridUtil class
- added background art for main menu screen

[Josh]
- Added check for full power to prevent grid lock
