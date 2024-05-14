package serialization;

import junit.framework.TestCase;

public class JsonEncoderTest extends TestCase {

    public void testEncode() {
        Test test = new Test();
        test.setName("this is name");
        test.setAge(19);
        Encoder encoder = new JsonEncoder();
        byte[] bytes = encoder.encode(test);

        assertNotNull(bytes);
    }
}