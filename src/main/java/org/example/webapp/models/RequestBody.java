package org.example.webapp.models;


import java.util.Objects;

public class RequestBody {
    private String msg;
    private Point body;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Point getBody() {
        return body;
    }

    public void setBody(Point body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestBody that = (RequestBody) o;
        return Objects.equals(msg, that.msg) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, body);
    }
}
