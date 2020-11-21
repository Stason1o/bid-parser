package parser.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BidWrapper {

    private Bid bid;

    public static BidWrapper from(Bid bid) {
        return new BidWrapper(bid);
    }
}
