public class ServiceImpl implements MyService{

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public String sayBye(String name) {
        return "Bye, " + name + "!";
    }
}
