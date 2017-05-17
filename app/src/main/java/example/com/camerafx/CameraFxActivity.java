package example.com.camerafx;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;


public class CameraFxActivity extends Activity {

    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;
    CameraPreview mPreview;
    Overlay mOverlay;

    // The first rear facing camera
    int defaultCameraId;
    private Camera.CameraInfo cameraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create our Preview view and set it as the content of our activity.
        // Create our DrawOnTop view.
        mOverlay = new Overlay(this);
        mPreview = new CameraPreview(this, mOverlay);
        setContentView(mPreview);

        addContentView(mOverlay,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Find the ID of the default camera
        cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(calcCameraRotation(270));
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }

    private int calcCameraRotation(int rotation) {
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (cameraInfo.orientation + rotation) % 360) % 360;
        } else {  // back-facing
            return (cameraInfo.orientation - rotation + 360) % 360;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

}