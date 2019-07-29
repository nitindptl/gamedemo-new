# Connects to Dot's game with two players

This is api for the connects to the dots with two players game.<br>
Below are the list of API :

## /INITIALIZE :

<p> This end point helps to initialize the game when player opens the index.html for the first time.</p>

## /node-clicked :

<p> This end point identifies which node is clicked based on the co-ordinates passed to it and keeps the track of all visited and non visited nodes and edges.<br>

    There are five acceptable values for msg in the response, representing the four possible states for a clicked node
    (VALID_START_NODE, INVALID_START_NODE, VALID_END_NODE, INVALID_END_NODE) and the game over state (GAME_OVER).
    A VALID_END_NODE must contain a LINE in the newLine field. If a VALID_END_NODE also constitutes the last move in the game,
    the Server should send GAME_OVER in the msg field instead. All other states should contain NULL in the newLine field.
</p>

## /error :

<p> This end point thrown when any error occurs while starting game. </p>


# To START with the GAME follow below procedure <br>

1. Build the Spring Boot application. <br>
2. Start Spring Boot Serve on default port 8080. <br>
3. Open client/index.html <br>
