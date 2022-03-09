**jrosclient** - command-line application which allows to interact with ROS (Robot Operating System).

# Download

You can download **jrosclient** from <https://github.com/lambdaprime/jrosclient/releases>

# Documentation

Documentation is available here <http://portal2.atwebpages.com/jrosclient/jrosclient-command.html>

# Usage

```bash
jrosclient [--masterUrl MASTER_URL] [--nodePort NODE_PORT] [--truncate MAX_LENGTH] [--debug] <COMMAND> [args ...]
```

Where:

MASTER_URL -- ROS master node URL

NODE_PORT -- client node port to use

COMMAND -- one of the client commands

Options:

--truncate - truncate objects logging

--debug - turns on debug mode

Available client commands:

rostopic list

rostopic echo [-n count] <topicName> <topicType>

# Contributors

lambdaprime <intid@protonmail.com>
