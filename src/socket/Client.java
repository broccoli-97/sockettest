package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by admin on 2017/5/30.
 */
public class Client {
    public static void main(String args[]){
        String msg="You are so beatiful";

        //创建一个socket，跟本机的8080端口建立连接
        try {

            Socket socket=new Socket("127.0.0.1",8080);
            //使用socket创建printWriter和BufferedReader进行读写数据
            PrintWriter printWriter=new PrintWriter(socket.getOutputStream());
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //发送数据
            printWriter.println(msg);
            printWriter.flush();

            //接受数据
            String line=bufferedReader.readLine();
            System.out.println("received from server "+line);
            //关闭资源
            printWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
