package parser.consumer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import parser.entity.Bid;

import java.util.Base64;
import java.util.concurrent.BlockingQueue;

@Log4j2
@RequiredArgsConstructor
public class QueueConsumer implements Runnable {

    private final String threadName;

    private final BlockingQueue<Bid> bidQueue;

    @Override
    public void run() {
        log.info("Initialized consumer {}", threadName);
        while (true) {
            consume();
            log.warn("THREAD {} is consuming queue...", threadName);
        }
    }

    @SneakyThrows
    private void consume() {
        Bid bid = bidQueue.take();
        log.info("Bid with id {}, timestamp {}, queue \"name\" {}, and payload \"{}\" was successfully received and processed",
                bid.getId(), bid.getTimestamp(), bid.getType(), new String(Base64.getDecoder().decode(bid.getPayload())));
    }
}
