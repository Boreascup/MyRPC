package server;

//import junit.framework.TestCase;
import protocol.Request;
import protocol.ServiceDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import common.utils.ReflectionUtils;
import java.lang.reflect.Method;

public class ServiceManagerTest {
    ServiceManager sm;

    @Before
    public void init(){
        sm = new ServiceManager();

        TestInterface bean = new TestClass();
        sm.register(TestInterface.class, bean);
    }

    @Test
    public void testRegister() {
        TestInterface bean = new TestClass();
        sm.register(TestInterface.class, bean);
    }

    @Test
    public void testLookup() {
        Method method = ReflectionUtils.getPublicMethods(TestInterface.class)[0];
        ServiceDescriptor sdp = ServiceDescriptor.from(TestInterface.class, method);

        Request request = new Request();
        request.setService(sdp);

        ServiceInstance sis = sm.lookup(request);
        Assert.assertNotNull(sis);
    }
}