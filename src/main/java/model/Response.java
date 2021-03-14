package model;

public class Response {

    private Result result;
    private String data;

    public Response() {
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "result=" + result +
                ", data='" + data + '\'' +
                '}';
    }
}
