package org.freedesktop.gstreamer.tutorials.application;

public class PipelineBuilder {

    static public String buildPipeline(String location){
        //String description=" filesrc location="+location + "  ! qtdemux " + "  ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0"; //dziala- samo audio
        //String description=" filesrc location=" +location+ "  ! qtdemux " + "  ! rtph264pay name=pay0"; //dziala wideo

        String description=" filesrc location="+location + "  ! qtdemux name=d d." +
                " ! queue "+"! rtph264pay name=pay0 d."
                + " !queue ! aacparse " + "  ! decodebin " + "  ! audioconvert " + "  ! avenc_aac " + "  ! rtpmp4apay name=pay0";

        return description;
    }
}
