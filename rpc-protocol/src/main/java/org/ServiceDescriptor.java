package org;
/**
 * ServiceDescriptor通常用于在客户端和服务端之间传递服务信息。
 * 当客户端想要调用一个远程服务时
 * 它会创建一个ServiceDescriptor实例来描述这个服务
 * 然后将这个实例发送到服务端。
 * 服务端收到这个实例后
 * 可以根据其中的信息找到对应的服务类和方法
 * 然后执行这个方法并将结果返回给客户端。
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor {
    private String clazz;
    private String method;
    private String returnType;
    private String[] parameterType;

    /**
     * 一个静态工厂方法。根据给定的类和方法创建一个ServiceDescriptor实例
     * @param clazz 类
     * @param method 类里的方法
     * @return ServiceDescriptor实例
     */
    public static ServiceDescriptor from(Class clazz, Method method){
        ServiceDescriptor sdp = new ServiceDescriptor();
        sdp.setClazz(clazz.getName());
        sdp.setMethod(method.getName());
        sdp.setReturnType(method.getReturnType().getName());

        Class[] parameterClasses = method.getParameterTypes();
        String[] parameterType = new String[parameterClasses.length];
        for(int i=0; i<parameterClasses.length; i++){
            parameterType[i] = parameterClasses[i].getName();
        }
        sdp.setParameterType(parameterType);
        return sdp;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        ServiceDescriptor that = (ServiceDescriptor) obj;
        return this.toString().equals(that.toString());
    }

    @Override
    public String toString() {
        return "class=" + clazz
                + ",method=" + method
                + ",returnType=" + returnType
                + ",parameterType=" + Arrays.toString(parameterType);
    }
}
