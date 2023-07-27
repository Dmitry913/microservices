import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        // наконец-то мне удалось раскусить, на какой ip-адрес внутри контейнера попадают запросы:)
        // фактически при отправке запроса в локальной сети по определённому порту, его обрабатывает дочерний процесс, который слушает этот порт,
        // далее запрос переадресуется на хост = префиксу идентификатора контейнера (он через ДНС всегда маппится в данный айпи адрес - 172.17.0.2)
        HttpServer server = HttpServer.create(new InetSocketAddress("172.17.0.2", 8000), 10);
        server.createContext("/health", createHealthHandler());
        server.start();
    }

    public static HttpHandler createHealthHandler() {
        return exchange -> {
            logger.info("Getting request from: " + exchange.getRemoteAddress());
            if (exchange.getRequestMethod().equals("GET")) {
                byte[] text = "{\"status\": \"OK\"}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, 16);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(text);
                outputStream.flush();
                logger.info("Request success handle");
            } else {
                logger.error("This request type doesn't handle");
            }
            exchange.close();
        };
    }

}
