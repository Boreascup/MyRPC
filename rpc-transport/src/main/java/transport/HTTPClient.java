package transport;

import lombok.extern.slf4j.Slf4j;
import org.Peer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HTTPClient implements TransportClient {
    private String url;

    @Override
    public void connect(Peer peer) {
        if(peer.isIPv6()){
            this.url = "http://[" + peer.getHost() + "]:" + peer.getPort();
        }
        else this.url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {
        try {
            log.info("url = {}", url);
            URL urlObj = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            // 设置Content-Type头
            httpConn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream out = httpConn.getOutputStream()) {
                IOUtils.copy(data, out);
            }

            int contentLength = httpConn.getContentLength();
            log.info("Content-Length: " + contentLength);

            int resultCode = httpConn.getResponseCode();
            log.info("resultCode = {}", resultCode);
            if (resultCode == HttpURLConnection.HTTP_OK) {
                return httpConn.getInputStream();
            } else {
                InputStream errorStream = httpConn.getErrorStream();
                if (errorStream != null) {
                    String errorMessage = IOUtils.toString(errorStream, StandardCharsets.UTF_8);
                    log.error("Server returned error: {}", errorMessage);
                } else {
                    log.error("Server returned error with no additional message");
                }
                return errorStream;
            }
        } catch (IOException e) {
            log.error("IOException during request: {}", e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        // 释放资源
    }
}
