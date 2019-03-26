package com.oasis.onebox.tool;

public class ResultShowing {

    private String resultInDesc;
    private Object result;

    public ResultShowing(String resultInDesc, Object result) {
        this.resultInDesc = resultInDesc;
        this.result = result;
    }

    public String getresultInDesc() {
        return resultInDesc;
    }

    public void setresultInDesc(String resultInDesc) {
        this.resultInDesc = resultInDesc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
