Tests for `jros2client` library.

# Prereqs

```
apt -y install ros-humble-ros-base
apt -y install ros-humble-demo-nodes-cpp
apt -y install ros-humble-rmw-cyclonedds-cpp
```

# Useful commands

- Publish tf message:
```
ros2 topic pub -r 3 helloRos tf2_msgs/msg/TFMessage '
transforms:
  - header:
      frame_id: "aaa"
    child_frame_id: "child"
    transform:
      translation:
        x: 1
        y: 2
        z: 3
      rotation:
        x: 1
        y: 2
        z: 3
        w: 4
'
```
