package protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Peer {
    private String host;
    private int port;
    public boolean isIPv6() {
        return host.contains(":");
    }
}
