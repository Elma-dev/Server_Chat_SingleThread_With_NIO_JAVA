package org.example;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static org.example.Main.handleReadWrite;

public class Server {
    public static void main(String[] args) throws Exception {
        //create Selector in mode open
        Selector selector= Selector.open();

        //create Server Socket Channel with mode non-blocking and register in selector and put it in mode accept only
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open(); //open is a method static can return new srv_sct_channel
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0",2001));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //get the new request in selector...after send it to server
        while(true){
            int count=selector.select(); //return how much of channel request 0,1,2..,4
            //if request doesn't existed
            if(count==0){
                continue;
            }
            //which keys selected
            Set<SelectionKey> selectionKey=selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator=selectionKey.iterator(); //iterator to read each key

            //read it
            while (selectionKeyIterator.hasNext()){
                SelectionKey key=selectionKeyIterator.next();
                //key isAcceptable or isReadable type we will analyse
                if(key.isAcceptable()){
                    //we accept the request, and we write a msg to client
                    AcceptNew(key,selector);
                } else if (key.isReadable()) {

                }
            }


        }

    }

    private static void AcceptNew(SelectionKey key, Selector selector) throws Exception {
        //get the server socket channel:
        ServerSocketChannel serverSocketChannel=(ServerSocketChannel) key.channel();
        //accept the request and create a socket channel of client:
        SocketChannel socketChannel =serverSocketChannel.accept();
        //mode non-blocking;
        socketChannel.configureBlocking(false);
        //socket Channel register in selector mode read only:
        socketChannel.register(selector,SelectionKey.OP_READ);

        //New Msg in the consol :
        System.out.println("New Connection From : "+socketChannel.getRemoteAddress());

    }

}
