
package com.oasis.onebox;

import android.widget.Button;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);
    private RegisterActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void EditTextTest(){

        EditText et = mActivity.findViewById(R.id.et_username);
        assertNotNull(et);

    }
    @Test
    public void Edit1TextTest(){

        EditText et1 = mActivity.findViewById(R.id.et_password);
        assertNotNull(et1);

    }
    @Test
    public void Edit2TextTest(){

        EditText et2 = mActivity.findViewById(R.id.et_repeatpassword);
        assertNotNull(et2);

    }
    @Test
    public void ButtonTest(){
        Button bt = mActivity.findViewById(R.id.bt_next);
        assertNotNull(bt);
    }
    @Test
    public void FadButtonTest(){
        Button bt1 = mActivity.findViewById(R.id.fab);
        assertNotNull(bt1);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}