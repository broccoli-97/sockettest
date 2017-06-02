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
