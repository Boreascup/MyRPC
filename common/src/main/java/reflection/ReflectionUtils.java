package reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    /**
     * 根据class创建对象
     * @param clazz 待创建对象的类
     * @return 创建好的对象
     * @param <T> 对象类型
     */
    public static <T> T newInstance(Class<T> clazz){
        try{
            return clazz.newInstance();
        }catch(Exception e){
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取某个类的公有方法们
     * @param clazz 一个类，比如“类名.class"
     * @return 当前类声明的公有方法的数组
     */
    public static Method[] getPublicMethods(Class clazz){
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> pmethods = new ArrayList<>();
        for(Method m : methods){
            if(Modifier.isPublic(m.getModifiers())){
                pmethods.add(m);
            }
        }
        return pmethods.toArray(new Method[0]);
    }

    /**
     * 调用指定对象的指定方法
     *
     * @param obj 被调用方法的对象
     * @param method 被调用的方法
     * @param args 传入的参数（可以是很多个,没有就不填
     * @return 返回结果
     */
    public static Object invoke(Object obj,
                                Method method,
                                Object... args){
        try{
            return method.invoke(obj, args);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
}
