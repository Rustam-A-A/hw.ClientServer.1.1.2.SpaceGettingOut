import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static final int SERVER_PORT = 10101;

    public static void main(String[] args) throws IOException {
        final int DELAY = 2000;

        //определяем сокет сервера
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", SERVER_PORT);
        final SocketChannel socketChannel = SocketChannel.open();

        //подключаемся к серверу
        socketChannel.connect(socketAddress);

        //получаем входящие и исходящие потоки информации
        try (Scanner scanner = new Scanner(System.in)){
            final ByteBuffer inputBuffer= ByteBuffer.allocate(2 << 10);
            String msg;
            while (true){
                System.out.println("Enter text to get rid of extra spaces:");
                msg = scanner.nextLine();
                if ("end".equals(msg)) break;
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                Thread.sleep(DELAY);
                int bytesCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0,
                        bytesCount, StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socketChannel.close();
        }
    }
}


