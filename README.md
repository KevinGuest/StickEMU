# StickEMU
Stick Arena (version 313) Server Files

1) Set up a webserver and MySQL database. XAMPP or similar is a good way to kill two birds with one stone here!

2) Copy the contents of "Web Stuff/htdocs" to your htdocs folder.

3) Open up "stick_arena.php" and set your database details in there (server is normally localhost). New as of 04/03/10 - Also open "crossdomain.xml" and set the IP to the server IP you are running on. 

4) Open up the folder "Client release (with tools). In there open up the file "flo.flm" and search for "http://YOUR_IP_HERE/stick_arena.php". Replace this with the desired URL of your stick_arena.php.

5) Run "assemble.bat". This will create your new client. Rename the client to "game_internet.swf".

6) Open up "settings313.ini". Change "&sServerName0=" to your desired server name, "&sServerURL0=" to your IP address, and "&sAdminMsgText=" to your desired server message.

7) Copy the "maps" folder, "game_internet.swf", "settings313.ini" and "languagefilter.ini" all over to your htdocs folder.

8) Using phpMyAdmin or similar, create a new database called "stick_arena". Once this is done, select the database, click import, then use the page to upload "stick_arena_structure.sql".

9) Open up the folder "StickEMU Server Release" and open config.properties. Change these to the credentials of your database. Also set the IP at which your server will be running in the server_IP field.

10) Run "run.bat" and you're done!



ERRORS:
1) "There was an error verifying your login" when attempting to log in:
The client cannot connect to your stick_arena.php file. Ensure your webserver is online and the URL is correctly placed in the client file (and you assembled it!)

2) "Pinging servers"
The game cannot connect to the specified server. Ensure the IP is correct in settings313.ini, ports are forwarded correctly (you need to forward port 1138) and the server is currently running.

3) "Incorrect username / password" upon login
Most likely your DB user and pass aren't correctly specified in the stick_arena.php file.

4) Java errors:
Ensure you have the latest JRE installed and the classpath is correct.
