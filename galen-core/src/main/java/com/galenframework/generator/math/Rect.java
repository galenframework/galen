/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.generator.math;


import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public Rect(Integer[] numbers) {
        if (numbers.length != 4) {
            throw new IndexOutOfBoundsException("Rect should take 4 arguments, got " +  numbers.length);
        }
        this.left = numbers[0];
        this.top = numbers[1];
        this.width = numbers[2];
        this.height = numbers[3];
    }

    @JsonIgnore
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

    @JsonIgnore
    public int getRight() {
        return left + width;
    }

    @JsonIgnore
    public int getBottom() {
        return top + height;
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
               
               points[1].setLeft(Math.max(points[1].getLeft(), pointsNext[1].getLeft()));
               points[1].setTop(Math.max(points[1].getTop(), pointsNext[1].getTop()));
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

    public int[] toIntArray() {
        return new int[]{getLeft(), getTop(), getWidth(), getHeight()};
    }

    public Rect offset(int offsetLeft, int offsetTop) {
        return new Rect(left + offsetLeft, top + offsetTop, width, height);
    }

    /**
     * Calculates the distance of given point to one of the rect edges.
     * If the point is located inside the return result will be negative.
     * If the point is located on edge the result will be zero
     * If the point is located outside of the rect - it will return positive value
     * @param point
     * @return
     */
    public int calculatePointOffsetDistance(Point point) {
        int right = left + width;
        int bottom = top + height;
        int pointLeft = point.getLeft();
        int pointTop = point.getTop();

        if (contains(point)) {
            return max(top - pointTop, pointTop - bottom, left - pointLeft, pointLeft - right);
        } else if (isQuadrant1(point)) {
            return max(abs(left - pointLeft), abs(top - pointTop));
        } else if (isQuadrant2(point)) {
            return abs(top - pointTop);
        } else if (isQuadrant3(point)) {
            return max(abs(pointLeft - right), abs(top - pointTop));
        } else if (isQuadrant4(point)) {
            return abs(pointLeft - right);
        } else if (isQuadrant5(point)) {
            return max(abs(pointLeft - right), abs(pointTop - bottom));
        } else if (isQuadrant6(point)) {
            return abs(pointTop - bottom);
        } else if (isQuadrant7(point)) {
            return max(abs(left - pointLeft), abs(pointTop - bottom));
        } else {
            return abs(left - pointLeft);
        }
    }

    private boolean isQuadrant1(Point point) {
        return point.getLeft() <= left && point.getTop() <= top;
    }
    private boolean isQuadrant2(Point point) {
        return point.getLeft() >= left && point.getLeft() <= getRight() && point.getTop() <= top;
    }
    private boolean isQuadrant3(Point point) {
        return point.getLeft() >= getRight() && point.getTop() <= top;
    }
    private boolean isQuadrant4(Point point) {
        return point.getLeft() >= getRight() && point.getTop() >= top && point.getTop() <= getBottom();
    }
    private boolean isQuadrant5(Point point) {
        return point.getLeft() >= getRight() && point.getTop() >= getBottom();
    }
    private boolean isQuadrant6(Point point) {
        return point.getTop() >= getBottom() && point.getLeft() >= left && point.getLeft() <= getRight();
    }
    private boolean isQuadrant7(Point point) {
        return point.getLeft() <= left && point.getTop() >= getBottom();
    }
    private boolean isQuadrant8(Point point) {
        return point.getLeft() <= left && point.getTop() >= top && point.getTop() <= getBottom();
    }


    public int max(int ... values) {
        if (values.length > 0) {
            int max = values[0];
            for (int i = 0; i < values.length; i++) {
                if (max < values[i]) {
                    max = values[i];
                }
            }
            return max;
        } else {
            throw new IllegalArgumentException("Empty array");
        }
    }

    public Rect drag(int offsetLeft, int offsetTop) {
        return new Rect(left + offsetLeft, top + offsetTop, width, height);
    }

    public Rect distort(int offsetLeft, int offsetTop, int offsetWidth, int offsetHeight) {
        return new Rect(left + offsetLeft, top + offsetTop, width + offsetWidth, height + offsetHeight);
    }
}

