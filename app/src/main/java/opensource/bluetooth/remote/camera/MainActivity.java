package opensource.bluetooth.remote.camera;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import opensource.bluetooth.remote.camera.fragment.Camera2Fragment;
import opensource.bluetooth.remote.camera.fragment.Camera2ServerFragment;
import opensource.bluetooth.remote.camera.interfaces.UpdateOutput;

public class MainActivity extends AppCompatActivity implements UpdateOutput {


    Camera2ServerFragment fragment = new Camera2ServerFragment();
    Camera2Fragment camera2Fragment = new Camera2Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_server, fragment);
            transaction.replace(R.id.fragment_client, camera2Fragment);
            transaction.commit();
        }
    }

    @Override
    public void updateOutput(SurfaceTexture surfaceTexture) {
        camera2Fragment.updateCameraBitMap(surfaceTexture);
    }
}
