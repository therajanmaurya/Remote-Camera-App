package opensource.bluetooth.remote.camera.fragment;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import opensource.bluetooth.remote.camera.R;
import opensource.bluetooth.remote.camera.interfaces.StreamCameraBitMap;

/**
 * Created by Rajan Maurya on 22/01/17.
 */

public class Camera2ClientFragment extends Fragment implements StreamCameraBitMap {

    @BindView(R.id.iv_stream)
    ImageView ivStream;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_camera_receiver, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void updateCameraBitMap(CameraCaptureSession cameraCaptureSession) {

    }

    @Override
    public void updateBitmapImage(Bitmap bitmap) {
        ivStream.setImageBitmap(bitmap);
    }
}
