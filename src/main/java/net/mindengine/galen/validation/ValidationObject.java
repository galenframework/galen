package net.mindengine.galen.validation;

import net.mindengine.galen.page.Rect;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ValidationObject {

    private Rect area;
    private String name;

    public ValidationObject(Rect area, String name) {
        this.area = area;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rect getArea() {
        return area;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(area)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ValidationObject))
            return false;

        ValidationObject rhs = (ValidationObject)obj;
        return new EqualsBuilder()
                .append(name, rhs.name)
                .append(area, rhs.area)
                .isEquals();
    }

    @Override
    public String toString() {
        return format("Object{name=%s, area=%s}", name, area);
    }
}
