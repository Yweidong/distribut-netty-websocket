package com.thought.monkey.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 * netty 服务器节点的信息
 */
@Data
public class WsNode implements Comparable<WsNode>, Serializable {

    private static final long serialVersionUID = 8167209449970958825L;


    //netty服务器工作的ID ，由zookeeper 生成
    private long id;

    private Integer balance = 0;


    //Netty 服务IP
    private String host = "127.0.0.1";

    //Netty 服务端口
    private Integer port = 8080;

    public WsNode() {
    }

    public WsNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "WsNode{" +
                "id=" + id +
                ", balance=" +balance +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WsNode wsNode = (WsNode) o;
        return id == wsNode.id &&
                Objects.equals(host, wsNode.host) &&
                Objects.equals(port, wsNode.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    @Override
    public int compareTo(WsNode o) {
        int weight1 = this.balance;
        int weight2 =balance;

        if (weight1 > weight2) {
            return 1;
        } else if (weight1 < weight2) {
            return  -1;
        }

        return 0;
    }


    public  void incrementBalance()
    {
        balance++;
    }

    public  void decrementBalance()
    {
        balance--;

    }
}
