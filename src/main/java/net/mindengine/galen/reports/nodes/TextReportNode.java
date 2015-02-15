package net.mindengine.galen.reports.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class TextReportNode extends TestReportNode {
    public TextReportNode(String details) {
        super();
        setName(details);
    }

    @JsonIgnore
    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @JsonIgnore
    @Override
    public Date getTime() {
        return super.getTime();
    }

    @JsonIgnore
    @Override
    public List<TestReportNode> getNodes() {
        return super.getNodes();
    }

    @Override
    public String getType() {
        return "text";
    }
}
