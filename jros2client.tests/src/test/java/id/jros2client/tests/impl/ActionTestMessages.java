/*
 * Copyright 2022 jrosclient project
 * 
 * Website: https://github.com/lambdaprime/jros2client
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.jros2client.tests.impl;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class ActionTestMessages {

    @MessageMetadata(name = TestActionGoalMessage.NAME, interfaceType = RosInterfaceType.ACTION)
    public static class TestActionGoalMessage implements Message {

        static final String NAME = "test/TestActionGoal";
    }

    @MessageMetadata(name = TestActionResultMessage.NAME, interfaceType = RosInterfaceType.ACTION)
    public class TestActionResultMessage implements Message {

        static final String NAME = "test/TestActionResult";
    }

    @MessageMetadata(
            name = TestActionGoalRequestMessage.NAME,
            interfaceType = RosInterfaceType.ACTION)
    public class TestActionGoalRequestMessage implements Message {

        static final String NAME = "action_msgs/CancelGoalServiceRequest";
    }

    @MessageMetadata(name = TestActionStatusMessage.NAME, interfaceType = RosInterfaceType.ACTION)
    public class TestActionStatusMessage implements Message {

        static final String NAME = "action_msgs/GoalStatusArray";
    }

    @MessageMetadata(name = TestActionFeedbackMessage.NAME, interfaceType = RosInterfaceType.ACTION)
    public class TestActionFeedbackMessage implements Message {

        static final String NAME = "test/TestActionGoalFeedback";
    }
}
