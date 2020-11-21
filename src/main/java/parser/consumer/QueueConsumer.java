package parser.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import parser.entity.Bid;

import java.util.Base64;
import java.util.Queue;

@Log4j2
@RequiredArgsConstructor
public class QueueConsumer implements Runnable {

    private final String threadName;

    private final Queue<Bid> bidQueue;

    @Override
    public void run() {
        log.info("Initialized consumer {}", threadName);
        while (true) {
            consume();
            log.trace("THREAD {} is consuming queue...", threadName);
        }
    }

    private void consume() {
        while (!bidQueue.isEmpty()) {
            Bid bid = bidQueue.poll();
            if (bid != null) {
                log.info("Bid with id {}, timestamp {}, queue \"name\" {}, and payload \"{}\" was successfully received and processed",
                        bid.getId(), bid.getTimestamp(), bid.getType(), new String(Base64.getDecoder().decode(bid.getPayload())));
            }
        }
    }
}
