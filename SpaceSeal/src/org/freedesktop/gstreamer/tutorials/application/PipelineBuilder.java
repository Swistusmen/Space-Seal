package org.freedesktop.gstreamer.tutorials.application;

public class PipelineBuilder {

    static public String buildPipeline(String location){
        String description="( filesrc location=" + location+" ! qtdemux name=d d. ! queue ! rtpgstpay ! pt=96 name=pay0 d. ! queue ! rtpmp4apay pt=97 name=pay1 )";
        //String description="( videotestsrc ! videoconvert ! x264enc ! rtph264pay name=pay0 pt=96 )";
        return description;
    }
}
