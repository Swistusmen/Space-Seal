package org.freedesktop.gstreamer.tutorials.application;

public class PipelineBuilder {

    static public String buildPipeline(String location){
        //String description=" filesrc location="+location + "  ! qtdemux " + "  ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0"; //dziala- samo audio
        //String description=" filesrc location=" +location+ "  ! qtdemux " + "  ! rtph264pay name=pay0"; //dziala wideo

        String RTSPStreamDescription=" filesrc location="+location + "  ! qtdemux name=d d." +
                " ! queue "+"! rtph264pay name=pay0 d."
                + " !queue ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0";

        String HLSStreamDescription=" filesrc location="+location+ " ! qtdemux name=demux ! queue ! h264parse ! mpegtsmux name=mux ! hlssink playlist-root=http://127.0.0.1:8080 max-files=100 playlist-length=5 demux. ! queue ! aacparse ! mux.";
        String ExperimentalAV1="filesrc location="+location+" ! qtdemux name=demux ! queue ! h264parse ! avdec_h264 ! av1enc ! matroskamux name=mux ! hlssink playlist-root=http://127.0.0.1:8080 max-files=100 playlist-length=0 demux. ! queue ! aacparse ! mux.";


        //This one streams mkv (av1,aac)
        String streamMKVAV1=" filesrc location=/storage/emulated/0/Movies/rabbitAV1.mkv !  hlssink playlist-root=http://127.0.0.1:8080 target-duration=5 max-files=100";
        //return ExperimentalAV1;
        return streamMKVAV1;
    }
}
