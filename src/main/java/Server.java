import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    public static final int SERVER_PORT = 10101;

    public static void main(String[] args) throws IOException {

        //занимаем порт, определяя серверный сокет
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", SERVER_PORT));

        while (true){
            //ждем подключение клиента и получаем потоки для двльнейшей работы
            try (SocketChannel socketChannel = serverChannel.accept()){

                //определяем буфер для получения данных
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2<<10);

                while (socketChannel.isConnected()){

                    //читаем данные из канала в буфер
                    int bytesCount = socketChannel.read(inputBuffer);

                    //если из потока читать нельзя, перестаем работать с этим клиентом
                    if (bytesCount == -1) break;

                    //получаем переданную от клента строку в нужной кодировке и очищаем буфер
                    final String msg = new String(inputBuffer.array(), 0,
                            bytesCount, StandardCharsets.UTF_8);
                    inputBuffer.clear();
                    System.out.println("Client's text received: " + msg);

                    //вызываем метод удаления лишних пробелов
                    ExtraTrim extraTrim = new ExtraTrim(msg);
                    String output = extraTrim.trimExtraSpaces();

                    //отправляем сообщение назад с пометкой Server
                    socketChannel.write(ByteBuffer.wrap(("Server: " + output).getBytes(StandardCharsets.UTF_8)));
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
