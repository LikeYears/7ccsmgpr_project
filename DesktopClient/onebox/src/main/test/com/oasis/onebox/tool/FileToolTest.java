package com.oasis.onebox.tool;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileToolTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isSameMd5() {
    }

    @Test
    public void isSameMd51() {
    }

    @Test
    public void compareImage() {
        String A= FileTool.compareImage("","");

        if (A !=null){
            System.out.println("success");
        }
        else {
            System.out.println("this function can't used!");
        }
    }

    @Test
    public void txtCompare() {
    }

    @Test
    public void getVersion() {

        String A= FileTool.getVersion("2.3.1");

        if (A.equals("2.3.4")){
            System.out.println("success");
        }
        else {
            System.out.println("this function can't used!");
    }}

    @Test
    public void main(){
    }
}
