package com.steinwurf.mediaplayer;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button playButton;
    private Surface mSurface = null;
    private VideoDecoder mVideoDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideUI();
        playButton = findViewById(R.id.playButton);
        playButton.setEnabled(false);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVideoPlayback();
            }
        });
        TextureView videoTextureView = findViewById(R.id.videoTextureView);
        videoTextureView.setSurfaceTextureListener(new TextureViewSurfaceTextureListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVideoPlayback();
    }

    private void startVideoPlayback() {
        VideoData videoData = new VideoData(this);
        if (mVideoDecoder != null)
            stopVideoPlayback();

        mVideoDecoder = VideoDecoder.build(
                videoData.width,
                videoData.height,
                videoData.sps,
                videoData.pps,
                videoData);

        mVideoDecoder.setSurface(mSurface);
        mVideoDecoder.start();
    }

    private void stopVideoPlayback() {
        if (mVideoDecoder != null)
        {
            mVideoDecoder.stop();
            mVideoDecoder = null;
        }
    }

    class TextureViewSurfaceTextureListener implements TextureView.SurfaceTextureListener
    {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            mSurface = new Surface(surfaceTexture);
            playButton.setEnabled(true);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (mSurface != null)
            {
                mSurface.release();
                mSurface = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }

    /**
     * Hides the UI of the application
     */
    private void hideUI()
    {
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        findViewById(R.id.mainRelativeLayout).setSystemUiVisibility(visibility);
    }
}
