import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import parser.consumer.QueueConsumer;
import parser.entity.Bid;
import parser.entity.BidType;
import parser.processor.FileProcessor;
import parser.scheduler.FileUpdateRunner;
import parser.service.WatchDirService;
import parser.util.PropertyReader;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import static parser.entity.BidType.AQ;
import static parser.entity.BidType.ZU;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        final PropertyReader propertyReader = new PropertyReader();
        final Properties properties = propertyReader.readPropertiesFromStream(Main.class.getResourceAsStream("app.properties"));
        final URI path = Main.class.getResource(properties.getProperty("file.source-name")).toURI();

        final Path dir = Paths.get(Paths.get(path).toFile().getParent());

        final Queue<Bid> aqBidQueue = new LinkedList<>();
        final Queue<Bid> zuBidQueue = new LinkedList<>();

        final QueueConsumer aqBidsConsumer = new QueueConsumer("AQ_THREAD", aqBidQueue);
        final QueueConsumer zuBidsConsumer = new QueueConsumer("ZU_THREAD", zuBidQueue);

        final Map<BidType, Queue<Bid>> bidQueues = ImmutableMap.of(AQ, aqBidQueue, ZU, zuBidQueue);

        final ObjectMapper objectMapper = new ObjectMapper();
        final FileProcessor fileProcessor = new FileProcessor(objectMapper, bidQueues);

        new Thread(aqBidsConsumer).start();
        new Thread(zuBidsConsumer).start();
        new Thread(new FileUpdateRunner(dir, "bids.json", objectMapper)).start();

        new WatchDirService(dir, fileProcessor, "bids.json").processEvents();
    }
}
