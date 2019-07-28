package org.example.webapp.models;

import java.util.Objects;

public class Response {

    private String msg;
    private InnerBody body;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public InnerBody getBody() {
        return body;
    }

    public void setBody(InnerBody body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response that = (Response) o;
        return Objects.equals(msg, that.msg) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, body);
    }
}
