
package com.galenframework.ocr.google.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoundingPoly {

    @SerializedName("vertices")
    @Expose
    private List<Vertex> vertices = null;

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

}
