/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.page;


import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Rect {

    private int left;
    private int width;
    private int top;
    private int height;

    public Rect(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public Point[] getPoints() {
        return new Point[]{new Point(left, top), 
                new Point(left + width, top),
                new Point(left + width, top + height),
                new Point(left, top + height)};
    }

    public boolean contains(Point point) {
        return (point.getLeft() >= left && point.getLeft() <= (left + width) 
                && point.getTop() >= top && point.getTop() <= (top + height));
    }
    
    public int getLeft() {
        return left;
    }
    public int getWidth() {
        return width;
    }
    public int getTop() {
        return top;
    }
    public int getHeight() {
        return height;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(left).append(top).append(width).append(height).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Rect))
            return false;
        
        Rect rhs = (Rect)obj;
        return new EqualsBuilder().append(left, rhs.left).append(top, rhs.top).append(width, rhs.width).append(height, rhs.height).isEquals();
    }
    
    @Override
    public String toString() {
        return format("Rect{left: %d, top: %d, w: %d, h: %d}", left, top, width, height);
    }

    public static Rect boundaryOf(Rect...rects) {
        if (rects.length > 0) {
            Point[] points = rects[0].getBoundaryPoints();
            
            for (int i=1; i< rects.length; i++) {
               Point[] pointsNext = rects[i].getBoundaryPoints();
               points[0].setLeft(min(points[0].getLeft(), pointsNext[0].getLeft()));
               points[0].setTop(min(points[0].getTop(), pointsNext[0].getTop()));
               
               points[1].setLeft(max(points[1].getLeft(), pointsNext[1].getLeft()));
               points[1].setTop(max(points[1].getTop(), pointsNext[1].getTop()));
            }
            return new Rect(points[0].getLeft(), points[0].getTop(), points[1].getLeft() - points[0].getLeft(), points[1].getTop() - points[0].getTop());
        }
        else return null;
    }

    private Point[] getBoundaryPoints() {
        return new Point[]{new Point(getLeft(), getTop()),
                new Point(getLeft() + getWidth(), getTop() + getHeight())
        };
    }

}

