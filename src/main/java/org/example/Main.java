package org.example;

import javax.xml.crypto.KeySelector;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception{
        //create Selector:
        Selector selector=Selector.open();
        //create a ServerSocketChannel
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        //before register the socketChanel in the selector we must switch to non bloc mode
        serverSocketChannel.configureBlocking(false);
        //add the parameter of server
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0",1234));
        //register in the selector with the mode of register (means what type of event we can listen(read-write-accept-connect))
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        while (true){
            //this method can to return number of channels ready for an operation.
            //This method blocks until at least one channel is ready for an operation.
            int nbChannels=selector.select();
            if(nbChannels==0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            System.out.println(nbChannels);

            while (iterator.hasNext()){
                SelectionKey selectionKey=iterator.next();
                if(selectionKey.isAcceptable()){
                    handleAccept(selector,selectionKey);
                }
                if(selectionKey.isReadable()){
                    handleReadWrite(selectionKey);
                }

                iterator.remove();
            }


        }
    }

    static void handleAccept(Selector selector,SelectionKey selectionKey) throws Exception{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel client = serverSocketChannel.accept();

        client.configureBlocking(false);
        client.register(selector,SelectionKey.OP_READ);

        System.out.println("-----------------------------------------");
        System.out.println("New Connection : "+client.getRemoteAddress().toString());
        System.out.println("Thread : "+Thread.currentThread().getName());

    }

    static void handleReadWrite(SelectionKey selectionKey) throws Exception{
        SocketChannel client=(SocketChannel) selectionKey.channel();
        ByteBuffer buffer=ByteBuffer.allocate(256);
        int numRead=client.read(buffer);
        if(numRead==-1){
            Socket socket=client.socket();
            System.out.println("Connection closed by client : "+socket.getInetAddress());

            socket.close();
            selectionKey.cancel();
            return;
        }
        //get msg
        String msg=new String(buffer.array());
        System.out.println("new message "+msg.trim()+" from "+client.getRemoteAddress().toString());

        //send resp

        String rsp="Hi\n";
        byte[] data=rsp.getBytes();
        ByteBuffer msgResp=ByteBuffer.wrap(data);
        client.write(msgResp);

    }
}