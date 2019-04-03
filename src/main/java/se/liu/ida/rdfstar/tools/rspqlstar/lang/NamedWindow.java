package se.liu.ida.rdfstar.tools.rspqlstar.lang;

import java.time.Duration;

public class NamedWindow {
    private String windowIri;
    private String streamIri;
    private Duration range;
    private Duration step;

    public NamedWindow(String windowIri, String streamIri, Duration range, Duration step){
        this.windowIri = windowIri;
        this.streamIri = streamIri;
        this.range = range;
        this.step = step;
    }

    public String getWindowName() {
        return windowIri;
    }

    public String getStreamName() {
        return streamIri;
    }

    public Duration getRange() {
        return range;
    }

    public Duration getStep() {
        return step;
    }
}
