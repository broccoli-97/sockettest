package niosocket;

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
 * Created by admin on 2017/5/31.
 */
public class NIOServer {
    public static void main(String args[]) throws IOException {
        //创建serversocketchannel，监听8080端口
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8081));

        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //为ssc注册选择器
        Selector selector=Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //创建处理器
        Handler handler=new Handler(1024);
        while (true){
            //等待请求，每次等待阻塞3秒，超过3秒后线程继续向下运行，如果传入0或不传参数
            //将一直阻塞
            if (selector.select(3000)==0){
                System.out.println("等待请求超时。。。。。。");
                continue;
            }
            System.out.println("处理请求");
            //获取待处理的SelectionKey
            Iterator<SelectionKey> keyIterator=selector.selectedKeys().iterator();

            while (keyIterator.hasNext()){
                SelectionKey key=keyIterator.next();
                try{
                //接受到连接请求时
                if(key.isAcceptable()){
                    handler.handleAccept(key);
                }
                //读数据
                if (key.isReadable()){
                    handler.handleRead(key);
                }
            }catch (IOException e){
                    keyIterator.remove();
                    continue;
                }
                //处理完后，从待处理的SelectionKey迭代器中移除当前所使用的Key
                keyIterator.remove();
        }
    }
    }
    public static class Handler {
        private int bufferSize = 1024;
        private String localCharset = "UTF-8";

        public Handler() {
        }

        public Handler(int bufferSize) {
            this(bufferSize, null);
        }

        public Handler(String LocalCharset) {
            this(-1, LocalCharset);
        }

        public Handler(int bufferSize, String localCharset) {
            if (bufferSize > 0)
                this.bufferSize = bufferSize;
            if (localCharset != null)
                this.localCharset = localCharset;
        }


        public void handleAccept(SelectionKey key) throws IOException {
            SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
        }

        public void handleRead(SelectionKey key) throws IOException {
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
                //将buffer转换为在读状态
                buffer.flip();
                //将buffer中接受到的值按localcharset格式编码后保存到receivestring中
                String receiveString= Charset.forName(localCharset).newDecoder().decode(buffer).toString();

                System.out.println("received from client "+receiveString);

                //返还数据给客户端
                String sendString ="received data: "+receiveString;
                buffer=ByteBuffer.wrap(sendString.getBytes(localCharset));
                socketChannel.write(buffer);
                //关闭socket
                socketChannel.close();
            }
        }
    }
}
