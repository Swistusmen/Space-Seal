package org.freedesktop.gstreamer.tutorials.application;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;


//ffmpeg -i rabbit.mp4 -c:v libaom-av1 -strict -2 rabbit.mkv

public class Transcoder {

    public void transcodeVideo(String oldVideoName, String newVideoName){
        newVideoName="storage/emulated/0/Movies/newestAmazingVideo.mkv";
        String command="-i "+oldVideoName+" -c:v libaom-av1 -strict -2 "+newVideoName;
        int rc = FFmpeg.execute(command);
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
    }}
}

