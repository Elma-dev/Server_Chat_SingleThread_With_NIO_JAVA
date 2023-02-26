package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        //connect to srv
        SocketChannel client =SocketChannel.open(new InetSocketAddress("localhost",2001));

        //scanner for user input
        Scanner scanner=new Scanner(System.in);

        //Thread for read message come in srv
        new Thread(()->{
            while (true){
                ByteBuffer buffer=ByteBuffer.allocate(1024);
                try {
                    client.read(buffer);
                    if(buffer.array().toString().length()>0){
                        System.out.println("srv msg => "+new String(buffer.array()).trim());
                    }
                }catch (Exception e){

                    try {
                        client.socket().close();
                    } catch (IOException ex) {
                    }
                }
            }
        }).start();

        //Write msg to srv
        while(true){
            String rsp=scanner.nextLine();
            ByteBuffer buffer=ByteBuffer.allocate(1024);

            buffer.put(rsp.getBytes());
            buffer.flip();
            client.write(buffer);
        }

    }

}
