package serialization;

import junit.framework.TestCase;

public class JsonDecoderTest extends TestCase {

    public void testDecode() {
        Test test = new Test();
        test.setName("this is name");
        test.setAge(19);
        Encoder encoder = new JsonEncoder();
        byte[] bytes = encoder.encode(test);

        Decoder decoder = new JsonDecoder();
        Test test2 = decoder.decode(bytes, Test.class);

        assertEquals("this is name", test2.getName());
        assertEquals(19, test2.getAge());
    }
}