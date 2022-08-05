# Lee Server

For any sane person I'd suggest NOT to read README if you don't want to see amateur documentation.

In any case. Welcome to land of nonsense.

## Server

Main class that will be handle connections

### main

This is the main function that requirers path to maps to be provided to run the whole app.

Path can look like eg. E:\maps

To launch the app you can your IDE or command prompt. To launch from IDE you'll need to provide path in args provided in arguments for app. If you are launching app from command prompt you can start it like: **java -cp . Server E:\maps**

In this function firstly will be checked path provided to the app using [check dir](#check-dir) function. If no path was specified the app will inform user and stop after 3 seconds. Nextly ServerSocket will be created, it will listen on your localhost IP and PORT (port can be changed default is 4000). After that Server will start listening for any connections **ENDLESSLY** until it is stope in IDE or command prompt is closed.

### check dir

This function will firstly check wether provided path is directory or a file itself. In case if path leads to the file it will check if name of file matches regex **s_map_.+.txt** (s_map_*any number*.txt) if so - it'll be the only awaitable map. If path leads to the directory [check_files](#check-files) function will be called. IF path is not a directory and does not match regex app will inform user and stop after 3 seconds.

### check files

Firstly this function will check if passed argument (boolean contains) is true.

If false it will walk directories and files searching for file with name matching regex **s_map_.+.txt** (s_map_*any number*.txt). If it doesn't find any it will inform user and close app after 3 seconds, in another case the function will call itself with argument = true.

If true function will simply add all matching files to list of available maps for the server.

### log

Simple System.out.println() function. It is used along all the classes and functions.

*Can be easily modified to gather session info and saving it later*

### get map

This function receives one String (map name), then searches for file with that name. Afterwards it will read the map to another String and gather length and width of the map. Afterwards will be called [place points](#place-points) function and returned as a result.

### place points

This function will be only called from [get map](#get-map) function. It receives String map, int height, int weight variables. To be put simply this function will **RANDOMLY** put player (@) and goal ($) into String of map.

## ClientConnectionThread

This class is  used to handle connection request of each client connected

### ClientConnectionThread

This constructor remembers client name and socket and starts [run](#run) to handle clients request provided after successful connection in [main](#main)

### run

Main body of the thread. In terms for Client-Server communication it will firstly send the list of available maps for the client. After in while(socket.isConnected()tru) loop it will listen for the request from client.

If request NOT equals DISCONNECT and line contains s_map server will call [get map](#get-map) function, send the map to user and receive client response.

Otherwise if request is DISCONNECT loop will be terminated, and finally will be used to close the connection.

## Summary

This is a shitty attempt to do a Client-Server connection.

Worth noticing that maps should be X by Y dimensions (no line should be longer or shorter) or it *might* break the algorithms.

They can look like:

++++++    </p>
++++++  ++</p>
    +   ++</p>
++++++++++</p>

And in program itself it will look like:

++++++|++++++  ++|+   ++|++++++++++

Bare in mind that there are spaces, but they are shortened by formatting
