cmake_minimum_required(VERSION 3.5)
project(custom_msgs)

# Default to C++14
if(NOT CMAKE_CXX_STANDARD)
  set(CMAKE_CXX_STANDARD 14)
endif()

find_package(ament_cmake REQUIRED)
find_package(rosidl_default_generators REQUIRED)
find_package(std_msgs REQUIRED)

set(MSG_FILES "msg/CharMessage.msg")
rosidl_generate_interfaces(${PROJECT_NAME} ${MSG_FILES} DEPENDENCIES std_msgs)
ament_export_dependencies(rosidl_default_runtime)

ament_package()