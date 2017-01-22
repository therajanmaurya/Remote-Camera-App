package opensource.bluetooth.remote.camera.interfaces;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraCaptureSession;

/**
 * Created by Rajan Maurya on 21/01/17.
 */

public interface StreamCameraBitMap {

    void updateCameraBitMap(CameraCaptureSession cameraCaptureSession);

    void updateBitmapImage(Bitmap bitmap);
}
