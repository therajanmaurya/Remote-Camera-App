package opensource.bluetooth.remote.camera;

/**
 * Created by Rajan Maurya on 18/01/17.
 */

public class Constants {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Message types sent from the BluetoothCameraService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothCameraService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static boolean DEVICE_TYPE = false;
}
