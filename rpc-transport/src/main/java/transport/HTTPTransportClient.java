package transport;

import lombok.extern.slf4j.Slf4j;
import org.Peer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HTTPTransportClient implements TransportClient{
    private String url;
    @Override
    public void connect(Peer peer) {
        this.url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {
        HttpURLConnection httpConn = null;
        try{
            log.info("url = {}", url);
            URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            // 设置Content-Type头
            httpConn.setRequestProperty("Content-Type", "application/json");

//            OutputStream out = httpConn.getOutputStream();
//            IOUtils.copy(data, out);
//            out.close();
            // 读取输入流内容到字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(data, byteArrayOutputStream);
            byte[] inputData = byteArrayOutputStream.toByteArray();

            // 打印输入流内容
            String inputDataString = new String(inputData, StandardCharsets.UTF_8);
            log.info("Request data: {}", inputDataString);

            // 将字节数组重新写入输出流
            try (OutputStream out = httpConn.getOutputStream()) {
                IOUtils.copy(new ByteArrayInputStream(inputData), out);
            }

            int resultCode = httpConn.getResponseCode();
            log.info("resultCode = {}", resultCode);
            if(resultCode == HttpURLConnection.HTTP_OK){
                return httpConn.getInputStream();
            }else {
                InputStream errorStream = httpConn.getErrorStream();
                String errorMessage = IOUtils.toString(errorStream, "UTF-8");
                log.error("Server returned error: {}", errorMessage);
                return errorStream;
            }
        }catch (IOException e){
            log.error("IOException during request: {}", e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {

    }
}
