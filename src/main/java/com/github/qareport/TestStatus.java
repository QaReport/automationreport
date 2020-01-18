package com.github.qareport;

public enum TestStatus {
    PASS("Pass"), FAIL("Fail");

    private String testStatus;

    private TestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    @Override
    public String toString() {
        return testStatus;
    }

}
