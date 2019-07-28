package org.example.webapp.models;

import java.io.Serializable;
import java.util.Objects;

public class InnerBody implements Serializable{
    private Line newLine;
    private String heading;
    private String message;


    public Line getNewLine() {
        return newLine;
    }

    public void setNewLine(Line line) {
        this.newLine = line;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InnerBody innerBody = (InnerBody) o;
        return Objects.equals(newLine, innerBody.newLine) &&
                Objects.equals(heading, innerBody.heading) &&
                Objects.equals(message, innerBody.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newLine, heading, message);
    }
}
