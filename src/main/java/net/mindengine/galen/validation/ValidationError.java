/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.validation;

import static java.lang.String.format;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.Rect;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ValidationError {

    private List<ValidationObject> objects = new LinkedList<ValidationObject>();
    private List<String> messages;
    private ImageComparison imageComparison;
    private boolean onlyWarn;

    public ValidationError(List<ValidationObject> objects, List<String> messages) {
        this.setObjects(objects);
        this.messages = messages;
    }

    public ValidationError(List<ValidationObject> objects, List<String> messages, ImageComparison imageComparison) {
        this.objects = objects;
        this.messages = messages;
        this.imageComparison = imageComparison;
    }

    public ValidationError() {
    }
        

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(objects)
                .append(messages)
                .append(imageComparison)
                .append(onlyWarn)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ValidationError))
            return false;
        
        ValidationError rhs = (ValidationError)obj;
        return new EqualsBuilder()
                .append(objects, rhs.objects)
                .append(messages, rhs.messages)
                .append(imageComparison, rhs.imageComparison)
                .append(onlyWarn, rhs.onlyWarn)
                .isEquals();
    }
    
    @Override
    public String toString() {
        return format("Error{%s, areas=%s, imageComparisonSample=%s, onlyWarn=%s}", messages, objects, imageComparison, onlyWarn);
    }


    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public ValidationError withMessage(String message) {
        if (messages == null) {
            messages = new LinkedList<String>();
        }
        messages.add(message);
        return this;
    }

    public void setOnlyWarn(boolean onlyWarn) {
        this.onlyWarn = onlyWarn;
    }

    public boolean isOnlyWarn() {
        return onlyWarn;
    }

    public ValidationError withOnlyWarn(boolean onlyWarn) {
        setOnlyWarn(onlyWarn);
        return this;
    }


    public ValidationError withImageComparisonSample(Rect area, String imagePath, BufferedImage bufferedImage) {
        setImageComparisonSample(new ImageComparison(area, imagePath, bufferedImage));
        return this;
    }

    public ImageComparison getImageComparison() {
        return imageComparison;
    }

    public void setImageComparisonSample(ImageComparison imageComparison) {
        this.imageComparison = imageComparison;
    }

    public List<ValidationObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ValidationObject> objects) {
        this.objects = objects;
    }
}
