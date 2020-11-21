package parser.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import parser.entity.Bid;
import parser.entity.BidType;
import parser.entity.BidWrapper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Log4j2
@RequiredArgsConstructor
public class FileProcessor {

    private final ObjectMapper objectMapper;

    private final Map<BidType, Queue<Bid>> bidQueues;

    @SneakyThrows
    public void processFile(final Path path) {
        final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(path.toFile()));

        final List<BidWrapper> bids = objectMapper.readValue(inputStream, new TypeReference<List<BidWrapper>>() {
        });

        final List<Bid> unprocessedBids = new ArrayList<>();

        bids.stream()
                .map(BidWrapper::getBid)
                .forEach(bid -> sendBid(bid, unprocessedBids));

        log.warn("Showing unprocessed bids[{}]", unprocessedBids.size());
        unprocessedBids.forEach(bid -> log.warn("Unprocessed bid: {}", bid));
    }

    private void sendBid(final Bid bid, final List<Bid> unprocessedBids) {
        if (!isParseable(bid.getType())) {
            log.warn("There is no consumer for this type of bid: {}.", bid.getType());
            unprocessedBids.add(bid);
        } else {
            bidQueues.get(BidType.valueOf(bid.getType())).add(bid);
            log.info("Adding bid of TYPE {} : {}", bid.getType(), bid);
        }
    }

    private boolean isParseable(final String bidType) {
        try {
            BidType.valueOf(bidType);
            return true;
        } catch (Exception ex) {
            log.warn("Received invalid type of bid: {}", bidType);
            return false;
        }
    }
}
