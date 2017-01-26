package opensource.bluetooth.remote.camera.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.Func;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import opensource.bluetooth.remote.camera.R;
import opensource.bluetooth.remote.camera.interfaces.UpdateOutput;
import opensource.bluetooth.remote.camera.view.AutoFitTextureView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 25/01/17.
 */

public class CameraFragment extends Fragment implements View.OnTouchListener {

    private static final String LOG_TAG = CameraFragment.class.getSimpleName();

    private static final String[] REQUEST_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION_CODE = 233;

    @BindView(R.id.texture)
    AutoFitTextureView mTextureView;
    private RxCamera camera;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, rootView);
        mTextureView.setOnTouchListener(this);
        if (!checkPermission()) {
            requestPermission();
        } else {
            openCamera();
        }
        requestSuccessiveData();
        return rootView;
    }


    @OnClick(R.id.fb_camera)
    void onClickCamera() {
        requestTakePicture();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : REQUEST_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), REQUEST_PERMISSIONS, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    private void openCamera() {
        RxCameraConfig config = new RxCameraConfig.Builder()
                .useBackCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(640, 480), false)
                .setHandleSurfaceEvent(true)
                .build();
        Log.d(LOG_TAG, "config: " + config);
        RxCamera.open(getActivity(), config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                camera = rxCamera;
                return rxCamera.bindTexture(mTextureView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.startPreview();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RxCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG , e.getMessage());
            }

            @Override
            public void onNext(final RxCamera rxCamera) {
                camera = rxCamera;
                Toast.makeText(getActivity(), "Now you can tap to focus", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestSuccessiveData() {
        if (!checkCamera()) {
            return;
        }
        camera.request().successiveDataRequest().subscribe(new Action1<RxCameraData>() {
            @Override
            public void call(final RxCameraData rxCameraData) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        YuvImage yuvImage = new YuvImage(rxCameraData.cameraData, ImageFormat.NV21, 160, 120, null);
                        yuvImage.compressToJpeg(new Rect(0, 0, 160, 120), 50, out);
                        byte[] imageBytes = out.toByteArray();
                        //Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        System.out.println("Writing data");

                        ((UpdateOutput) getActivity()).updateOutput(imageBytes);
                    }
                }, 30);

            }
        });
    }

    private void requestPeriodicData() {
        if (!checkCamera()) {
            return;
        }
        camera.request().periodicDataRequest(1000).subscribe(new Action1<RxCameraData>() {
            @Override
            public void call(RxCameraData rxCameraData) {

            }
        });
    }

    private void requestTakePicture() {
        if (!checkCamera()) {
            return;
        }
        camera.request().takePictureRequest(true, new Func() {
            @Override
            public void call() {
                Log.d(LOG_TAG, "Captured!");
            }
        }, 480, 640, ImageFormat.JPEG, true).subscribe(new Action1<RxCameraData>() {
            @Override
            public void call(RxCameraData rxCameraData) {
                String path = Environment.getExternalStorageDirectory() + "/test.jpg";
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeByteArray(rxCameraData.cameraData, 0, rxCameraData.cameraData.length);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        rxCameraData.rotateMatrix, false);
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(LOG_TAG, "Save file on " + path);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (camera != null) {
            camera.closeCamera();
        }
    }

    private boolean checkCamera() {
        if (camera == null || !camera.isOpenCamera()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public byte[] convertYuvToJpeg(byte[] data, Camera camera) {

        YuvImage image = new YuvImage(data, ImageFormat.NV21,
                camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 20; //set quality
        image.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width,
                camera.getParameters().getPreviewSize().height), quality, baos);//this line decreases the image quality


        return baos.toByteArray();
    }
}
