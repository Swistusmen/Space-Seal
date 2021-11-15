package org.freedesktop.gstreamer.tutorials.application;

import android.util.Log;

import com.arthenica.mobileffmpeg.Config;

public class PipelineBuilder {

    static public String buildPipeline(String location, StreammingProtocol protocol,VideoCodecs codec){
        //String description=" filesrc location="+location + "  ! qtdemux " + "  ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0"; //dziala- samo audio
        //String description=" filesrc location=" +location+ "  ! qtdemux " + "  ! rtph264pay name=pay0"; //dziala wideo

        if(StreammingProtocol.RTSP==protocol) {
            if(VideoCodecs.H264==codec){
            String RTSPStreamDescription = " filesrc location=" + location + "  ! qtdemux name=d d." +
                    " ! queue " + " rtph264pay name=pay0 d."
                    + " !queue ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0";
            return RTSPStreamDescription;
            }else if(codec==VideoCodecs.AV1){
                String RTSPStreamDescription = " filesrc location=" + location + "  ! matroskademux name=d d." +
                        " ! queue " + " rtph264pay name=pay0 d."
                        + " !queue ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0";
                return RTSPStreamDescription;
            }
        }

        if(protocol==StreammingProtocol.HLS) {
            //if(codec==VideoCodecs.AV1){
            String streamMKVAV1 = " filesrc location=" + location + " !  hlssink playlist-root=http://127.0.0.1:8080 target-duration=5 max-files=100";
            return streamMKVAV1;


        }
        return "";
    }
}
