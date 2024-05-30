package protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    public static ServiceDescriptor from(Class<?> clazz, Method method) {
        ServiceDescriptor descriptor = new ServiceDescriptor();
        descriptor.setClazz(clazz.getName());
        descriptor.setMethod(method.getName());
        descriptor.setReturnType(method.getReturnType().getName());

        Class<?>[] paramClasses = method.getParameterTypes();
        String[] paramTypes = new String[paramClasses.length];
        for (int i = 0; i < paramClasses.length; i++) {
            paramTypes[i] = paramClasses[i].getName();
        }
        descriptor.setParameterType(paramTypes);

        return descriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, method, returnType, Arrays.hashCode(parameterType));
    }

    //重写equals是因为map会调用此方法来判断是否有重复元素
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ServiceDescriptor that = (ServiceDescriptor) obj;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(method, that.method) &&
                Objects.equals(returnType, that.returnType) &&
                Arrays.equals(parameterType, that.parameterType);
    }

    @Override
    public String toString() {
        return "class=" + clazz
                + ",method=" + method
                + ",returnType=" + returnType
                + ",parameterType=" + Arrays.toString(parameterType);
    }
}
