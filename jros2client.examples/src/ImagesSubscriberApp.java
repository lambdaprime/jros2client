import id.jros2client.JRos2ClientFactory;
import id.jros2messages.sensor_msgs.ImageMessage;
import id.jrosclient.TopicSubscriber;

/**
 * Subscribes to ROS image_publisher node.
 * 
 * <p>Start image_publisher:
 * 
 * <pre>
 * apt install -y ros-humble-image-publisher
 * ros2 launch image_publisher image_publisher_file.launch.py
 * </pre>
 *
 */
public class ImagesSubscriberApp {

    public static void main(String[] args) throws Exception {
        var client = new JRos2ClientFactory().createClient();
        var topicName = "/camera/image_raw";
        // register a new subscriber
        client.subscribe(
                new TopicSubscriber<>(ImageMessage.class, topicName) {
                    @Override
                    public void onNext(ImageMessage item) {
                        System.out.println(item);
                        // request next message
                        getSubscription().get().request(1);
                    }
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing messages indefinitely
    }
}
