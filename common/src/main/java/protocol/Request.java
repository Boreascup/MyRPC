package protocol;

import lombok.Data;

@Data
public class Request {
    private ServiceDescriptor service;
    private Object[] parameters;
}
