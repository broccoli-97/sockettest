package http;

import niosocket.NIOServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by admin on 2017/6/2.
 */
public class HttpServer {

    public static void main(String args[]) throws IOException {
        //创建serversocketchannel，监听8080端口
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));

        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //为ssc注册选择器
        Selector selector=Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //创建处理器
        while (true){
            //等待请求，每次等待阻塞3秒，超过3秒后线程继续向下运行，如果传入0或不传参数
            //将一直阻塞
            if (selector.select(3000)==0){
                continue;
            }
            //获取待处理的SelectionKey
            Iterator<SelectionKey> keyIterator=selector.selectedKeys().iterator();

            while (keyIterator.hasNext()){
                SelectionKey key=keyIterator.next();
                //启动新的线程处理器
                new Thread(new HttpHandler(key)).run();
                //处理完后，从待处理的SelectionKey迭代器中移除当前所使用的Key
                keyIterator.remove();
            }
        }
    }
    private static class HttpHandler implements Runnable{
        private int bufferSize = 1024;
        private String localCharset = "UTF-8";
        private SelectionKey key;

        public HttpHandler(SelectionKey key){
            this.key=key;
        }


        public void handleAccept() throws IOException {
            SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
        }

        public void handleRead() throws IOException {
            //获取channel
            SocketChannel socketChannel=(SocketChannel)key.channel();
            //获取buffer并重置
            ByteBuffer buffer= (ByteBuffer) key.attachment();
            buffer.clear();
            //没有读到内容就关闭
            if(socketChannel.read(buffer)==-1){
                socketChannel.close();
            }
            else{
                //接收请求数据
                buffer.flip();
                String receivedString =Charset.forName(localCharset).newDecoder().decode(buffer).toString();
                //控制台打印请求报文头
                String[] requestMessage=receivedString.split("\r\n");
                for (String s:requestMessage){
                    System.out.println(s);
                    //遇到空行说明报文头已经打印完
                    if (s.isEmpty())
                        break;
                }

                //控制台打印首行信息
                String [] firstline=requestMessage[0].split(" ");
                System.out.println();
                System.out.println("Method:\t"+firstline[0]);
                System.out.println("url:\t"+firstline[1]);
                System.out.println("HTTP Version:\t"+firstline[2]);
                System.out.println();


                //返回客户端
                StringBuilder sendString =new StringBuilder();
                sendString.append("HTTP/1.1 200 OK\r\n");//请求报文首行，200表示处理成功
                sendString.append("Content-Type:text/html;charset="+localCharset+"\r\n");
                sendString.append("\r\n");//报文接受后加一个空行

                sendString.append("<html><head><title>显示报文</title></head><body>");
                sendString.append("接收到的请求是: <br/>");
                for (String s:requestMessage){
                    sendString.append(s+"<br/>");
                }
                sendString.append("</body></html>");
                buffer=ByteBuffer.wrap(sendString.toString().getBytes(localCharset));
                socketChannel.write(buffer);
                socketChannel.close();

            }
        }

        @Override
        public void run() {
            try {
                //接收到连接请求时
                if (key.isAcceptable()){
                    handleAccept();
                }
                //读取数据
                if (key.isReadable()){
                    handleRead();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
