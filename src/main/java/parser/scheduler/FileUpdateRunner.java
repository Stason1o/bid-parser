package parser.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import parser.entity.Bid;
import parser.entity.BidWrapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.Base64.getEncoder;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.random;

@Log4j2
@RequiredArgsConstructor
public class FileUpdateRunner implements Runnable {

    private final Path filePath;

    private final String fileSource;

    private final ObjectMapper objectMapper;

    private List<Bid> bids = new ArrayList<>();

    private List<String> bidTypes = asList("AQ", "ZU", "INVALID");

    {
        bids.addAll(newArrayList(
                new Bid(randomUUID().toString(),
                        now().minusDays(new Random().nextInt(20)).format(ofPattern("yyyyMMddhhmmss")),
                        bidTypes.get(new Random().nextInt(bidTypes.size())),
                        new String(getEncoder().encode(random(15).getBytes()))),
                new Bid(randomUUID().toString(),
                        now().minusDays(new Random().nextInt(20)).format(ofPattern("yyyyMMddhhmmss")),
                        bidTypes.get(new Random().nextInt(bidTypes.size())),
                        new String(getEncoder().encode(random(15).getBytes()))),
                new Bid(randomUUID().toString(),
                        now().minusDays(new Random().nextInt(20)).format(ofPattern("yyyyMMddhhmmss")),
                        bidTypes.get(new Random().nextInt(bidTypes.size())),
                        new String(getEncoder().encode(random(15).getBytes()))),
                new Bid(randomUUID().toString(),
                        now().minusDays(new Random().nextInt(20)).format(ofPattern("yyyyMMddhhmmss")),
                        bidTypes.get(new Random().nextInt(bidTypes.size())),
                        new String(getEncoder().encode(random(15).getBytes()))),
                new Bid(randomUUID().toString(),
                        now().minusDays(new Random().nextInt(20)).format(ofPattern("yyyyMMddhhmmss")),
                        bidTypes.get(new Random().nextInt(bidTypes.size())),
                        new String(getEncoder().encode(random(15).getBytes())))
        ));
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            Thread.sleep(60000);
            log.info("UPDATING FILE..........");

            bids.add(new Bid(randomUUID().toString(),
                    now().format(ofPattern("yyyyMMddhhmmss")),
                    bidTypes.get(new Random().nextInt(bidTypes.size() - 1)),
                    new String(getEncoder().encode(random(15).getBytes()))));

            final List<BidWrapper> bidWrappers = bids.stream().map(BidWrapper::from).collect(toList());

            final Path path = filePath.resolve(fileSource);

            Files.write(path, objectMapper.writeValueAsBytes(bidWrappers));
            log.info("File is successfully updated!");
        }
    }
}
