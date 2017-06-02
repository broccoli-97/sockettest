package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by admin on 2017/5/30.
 */
public class Server {
    public static void main(String args[]){
        //首先创建一个serversocket监听8080端口
        try {
            ServerSocket serverSocket=new ServerSocket(8080);
            //等待请求
            Socket socket=serverSocket.accept();
            //收到请求后使用socket进行通信，创建BufferedReader用于读取数据
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line=bufferedReader.readLine();
            System.out.println("received from client "+line);

            //创建PrintWriter用于发送数据
            PrintWriter printWriter=new PrintWriter(socket.getOutputStream());
            printWriter.println("received data "+line);
            printWriter.flush();
            //关闭资源
            printWriter.close();;
            bufferedReader.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
