package opensource.bluetooth.remote.camera;

import android.media.Image;

import java.nio.ByteBuffer;

/**
 * Created by Rajan Maurya on 22/01/17.
 */

public class Utils {

    private byte[] convertYUV420ToNV21(Image imgYUV420) {
        byte[] rez;

        ByteBuffer buffer0 = imgYUV420.getPlanes()[0].getBuffer();
        ByteBuffer buffer2 = imgYUV420.getPlanes()[2].getBuffer();
        int buffer0_size = buffer0.remaining();
        int buffer2_size = buffer2.remaining();
        rez = new byte[buffer0_size + buffer2_size];

        buffer0.get(rez, 0, buffer0_size);
        buffer2.get(rez, buffer0_size, buffer2_size);

        return rez;
    }
}
