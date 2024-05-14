package common.utils;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class ReflectionUtilsTest extends TestCase {

    public void testNewInstance() {
        Test t = ReflectionUtils.newInstance(Test.class);
        assertNotNull(t);
    }

    public void testGetPublicMethods() {
        Method[] methods = ReflectionUtils.getPublicMethods(Test.class);
        assertEquals(1, methods.length);
        assertEquals("c", methods[0].getName());
    }

    public void testInvoke() {
        Method[] methods = ReflectionUtils.getPublicMethods(Test.class);
        Method b = methods[0];
        Test t = new Test();
        Object res = ReflectionUtils.invoke(t, b);
        assertEquals("c", res);
    }
}