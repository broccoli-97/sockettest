# sockettest
<p>分别试了下socket和Niosocket
使用测试时先启动server，再启动client</p>
<h4>Niosocket简单说明</h4>
<p>Niosocket使用时有三个概念</p>
<ul>
<li>Buffer</li>
<li>Channel</li>
<li>Selector</li>
</ul>
<p>Niosocket处理模式适用于请求比较频繁的时候，通过Selector“分拣”之后统一“发送”，效率比较高</p>
<br/>
<p>添加了一个在NIOserver的基础上自己做的一个简单实现了HTTP协议的例子</p>
<p>HTTP协议是在应用层解析内容，只需按照它的报文的格式封装和解析数据就可以，具体的传输还是使用的socket</p>
<p>只需修改NIOserver中的Handler，首先获取到请求报文并打印出报文的头部（包括首行）、请求的方法类型、url和http版本，最后将接收到的请求报文信息封装在响应报文中返回给客户端，HttpHandler使用了单独的线程来执行，而且把Selectionkey中操作类型的选择也放在了HttpHandler中</p>

<h6>注：参考《看透spring mvc》</h6>
