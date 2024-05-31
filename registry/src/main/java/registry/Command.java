package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

// 命令模式的抽象命令类
public interface Command {
    void execute(BufferedReader in, PrintWriter out) throws IOException;
}
