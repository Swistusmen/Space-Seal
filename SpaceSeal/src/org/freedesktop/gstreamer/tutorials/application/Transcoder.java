package org.freedesktop.gstreamer.tutorials.application;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.util.Log;
import android.util.Pair;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

public class Transcoder {

    public Pair<Integer, Integer> transcodeVideo(String filename, VideoCodecs codec){
        String newVideoName=getNewFileName(filename);
        if(codec==VideoCodecs.AV1){
            long start=System.currentTimeMillis();
            return new Pair<Integer,Integer>(transcodeToAV1(filename,newVideoName), (int) ((System.currentTimeMillis()-start)/1000));
        }else if(codec==VideoCodecs.H264){
            long start=System.currentTimeMillis();
            return new Pair<Integer,Integer>(transcodeToH264(filename,newVideoName), (int) ((System.currentTimeMillis()-start)/1000));
        }

        return new Pair<Integer,Integer>(0,0);
    }

    private int transcodeToH264(String oldVideoName,String newVideoName) {
        String command="-i "+oldVideoName+" "+newVideoName;
        int rc = FFmpeg.execute(command);
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
        return rc;
    }

    public int transcodeToAV1(String oldVideoName, String newVideoName){
        String command="-i "+oldVideoName+" -c:v libaom-av1 -strict -2 -c:a aac "+newVideoName;
        int rc = FFmpeg.execute(command);
        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO); }
        return rc;
    }

    private String getNewFileName(String filename){
        Log.i(Config.TAG, filename);
        String []parts=filename.split(".");
        //String newVideoName=parts[0]+".mkv";
        String newVideoName="/storage/emulated/0/Movies/myTest.mkv";
        Log.i(Config.TAG, newVideoName);
        return newVideoName;
    }

}

