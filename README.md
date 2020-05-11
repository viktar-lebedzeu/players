## Given requirements.

1. create 2 Player instances
2. one of the players should send a message to second player (let's call this player "initiator")
3. when a player receives a message, it should reply with a message that contains the received message concatenated with the value of a counter holding the number of messages this player already sent.
4. finalize the program (gracefully) after the initiator sent 10 messages and received back 10 messages (stop condition)
5. both players should run in the same java process (strong requirement)
6. document for every class the responsibilities it has.
7. additional challenge (nice to have) opposite to 5: have every player in a separate JAVA process.

## Compiling application
Run the following command:
```
mvn clean install
```

## Run the application

Application could be run in one of the following modes:
+ all players run in the same java process
+ every player run in a separate java process

### Application options
```
====================================================================================================
Players application options
====================================================================================================
 -ch,--channel <Channel>            Type of channel (default or socket). Default will be used if omitted
 -h,--help                          Prints help message.
 -msg,--message <Messages>          Comma separated list of messages.
 -p,--players <Players>             Comma separated list of Players. First of them will be initiator.
 -sub,--subscribers <Subscribers>   Count of subscribers to start message exchange in socket mode.
 -to,--timeout <Timeout (sec)>      Start timeout for socket mode in seconds. 180 by default.
====================================================================================================
```

Using options described above we are able to configure and run the application in different modes.
 
### Running all players in the same java process
```batch
java -jar target\players-jar-with-dependencies.jar -p master,player_1,player_2,player_3 -msg "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten"
```
or run
```batch
start-in-same-process.cmd
```
##### Sample log
```text
Secondary player "player_1" received channel ready event.
Secondary player "player_2" received channel ready event.
Primary player "master": channel is ready. Starting conversation.
Secondary player "player_3" received channel ready event.
Secondary player "player_3" received message: "One"
Secondary player "player_2" received message: "One"
Secondary player "player_1" received message: "One"
Primary player "master" received message "One 1" from "player_3"
Primary player "master" received message "One 1" from "player_2"
Primary player "master" received message "One 1" from "player_1"
Secondary player "player_3" received message: "Two"
Secondary player "player_1" received message: "Two"
Secondary player "player_2" received message: "Two"
Primary player "master" received message "Two 2" from "player_3"
Primary player "master" received message "Two 2" from "player_1"
Primary player "master" received message "Two 2" from "player_2"
Secondary player "player_1" received message: "Three"
Secondary player "player_3" received message: "Three"
Secondary player "player_2" received message: "Three"
Primary player "master" received message "Three 3" from "player_3"
Primary player "master" received message "Three 3" from "player_1"
Primary player "master" received message "Three 3" from "player_2"
Secondary player "player_3" received message: "Four"
Secondary player "player_1" received message: "Four"
Secondary player "player_2" received message: "Four"
Primary player "master" received message "Four 4" from "player_3"
Primary player "master" received message "Four 4" from "player_1"
Primary player "master" received message "Four 4" from "player_2"
Secondary player "player_3" received message: "Five"
Secondary player "player_2" received message: "Five"
Secondary player "player_1" received message: "Five"
Primary player "master" received message "Five 5" from "player_3"
Primary player "master" received message "Five 5" from "player_2"
Primary player "master" received message "Five 5" from "player_1"
Secondary player "player_2" received message: "Six"
Secondary player "player_3" received message: "Six"
Secondary player "player_1" received message: "Six"
Primary player "master" received message "Six 6" from "player_2"
Primary player "master" received message "Six 6" from "player_3"
Primary player "master" received message "Six 6" from "player_1"
Secondary player "player_1" received message: "Seven"
Secondary player "player_3" received message: "Seven"
Secondary player "player_2" received message: "Seven"
Primary player "master" received message "Seven 7" from "player_1"
Primary player "master" received message "Seven 7" from "player_2"
Primary player "master" received message "Seven 7" from "player_3"
Secondary player "player_1" received message: "Eight"
Secondary player "player_2" received message: "Eight"
Secondary player "player_3" received message: "Eight"
Primary player "master" received message "Eight 8" from "player_1"
Primary player "master" received message "Eight 8" from "player_2"
Primary player "master" received message "Eight 8" from "player_3"
Secondary player "player_3" received message: "Nine"
Secondary player "player_2" received message: "Nine"
Secondary player "player_1" received message: "Nine"
Primary player "master" received message "Nine 9" from "player_3"
Primary player "master" received message "Nine 9" from "player_2"
Primary player "master" received message "Nine 9" from "player_1"
Secondary player "player_3" received message: "Ten"
Secondary player "player_2" received message: "Ten"
Secondary player "player_1" received message: "Ten"
Primary player "master" received message "Ten 10" from "player_3"
Primary player "master" received message "Ten 10" from "player_2"
Primary player "master" received message "Ten 10" from "player_1"
Started shooting down the channel
Secondary player "player_1" received shoot down event.
Secondary player "player_2" received shoot down event.
Primary player "master" received shootdown event.
Secondary player "player_3" received shoot down event.
Channel bus task completed.
```
 
### Running every player in a separate java process

Interprocess communication has been implemented using socket connection.
Other implementations could be added in the future.
To allow this mode application must be run using `-ch socket:<HOST>:<PORT>` option. 

#### Run server instance
```batch
java -jar target\players-jar-with-dependencies.jar -ch socket:localhost:6000 -p master -msg "One,Two,Three,Four,Five,Six,Seven,Eight,Nine,Ten" --timeout 60 -sub 1
```
or 
```batch
start-server.cmd
```

##### Sample server log:
```text
Socket opened : FYLlFgeUH9yIBcDrytDANmdFoI4JZi6lqXGLQTKhGcpUE1owlW
Player player_1 subscribed.
Primary player "master": channel is ready. Starting conversation.
Primary player "master" received message "One 1" from "player_1"
Primary player "master" received message "Two 2" from "player_1"
Primary player "master" received message "Three 3" from "player_1"
Primary player "master" received message "Four 4" from "player_1"
Primary player "master" received message "Five 5" from "player_1"
Primary player "master" received message "Six 6" from "player_1"
Primary player "master" received message "Seven 7" from "player_1"
Primary player "master" received message "Eight 8" from "player_1"
Primary player "master" received message "Nine 9" from "player_1"
Primary player "master" received message "Ten 10" from "player_1"
Started shooting down the channel
Execution interrupted. Shooting down.
Socket FYLlFgeUH9yIBcDrytDANmdFoI4JZi6lqXGLQTKhGcpUE1owlW lost connection
Socket closed : FYLlFgeUH9yIBcDrytDANmdFoI4JZi6lqXGLQTKhGcpUE1owlW
Socket channel bus task completed.
```

#### Run client instance
```batch
java -jar target\players-jar-with-dependencies.jar -ch socket:localhost:6000 -p player_1
```
or 
```batch
start-client.cmd
```

##### Sample client log:
```text
Secondary player "player_1" received channel ready event.
Secondary player "player_1" received message: "One"
Secondary player "player_1" received message: "Two"
Secondary player "player_1" received message: "Three"
Secondary player "player_1" received message: "Four"
Secondary player "player_1" received message: "Five"
Secondary player "player_1" received message: "Six"
Secondary player "player_1" received message: "Seven"
Secondary player "player_1" received message: "Eight"
Secondary player "player_1" received message: "Nine"
Secondary player "player_1" received message: "Ten"
Socket kLWioJktDGASvhl0FW1aSoHYvtRYCxhJPJANdNXvJgrEmICs8q lost connection
Socket closed : kLWioJktDGASvhl0FW1aSoHYvtRYCxhJPJANdNXvJgrEmICs8q
Socket channel bus task completed.
```
