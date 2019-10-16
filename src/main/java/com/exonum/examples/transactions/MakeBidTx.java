package com.exonum.examples.transactions;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.transaction.RawTransaction;
import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.TransactionContext;
import com.exonum.binding.core.transaction.TransactionExecutionException;
import com.exonum.examples.Schema;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
import com.exonum.examples.model.Auction;
import com.exonum.examples.model.Bid;
import com.exonum.examples.model.User;

public class MakeBidTx implements Transaction {
  public static final short ID = 3;

  private int auctionId;
  private long value;

  public MakeBidTx(int auctionId, long value) {
    this.auctionId = auctionId;
    this.value = value;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    Schema schema = new Schema(transactionContext.getFork());
    PublicKey author = transactionContext.getAuthorPk();

    if (schema.getUsers().get(author) == null)
      throw new TransactionExecutionException(ErrorCodes.USER_NOT_FOUND);

    User user = new User(schema.getUsers().get(author));

    Auction auction;
    try {
      auction = new Auction(schema.getAuctions().get(auctionId));
    } catch (IllegalArgumentException e) {
      throw new TransactionExecutionException(ErrorCodes.AUCTION_NOT_FOUND);
    }

    if (auction.isClosed())
      throw new TransactionExecutionException(ErrorCodes.AUCTION_CLOSED);

    if (user.getBalance() < value)
      throw new TransactionExecutionException(ErrorCodes.INSUFFICIENT_FUNDS);

    if (author.equals(auction.getOwner()))
      throw new TransactionExecutionException(ErrorCodes.SELF_BIDDING);

    long bidToBeat;
    try {
      bidToBeat = schema.getAuctionBids(auctionId).getLast().getValue();
    } catch (Exception e) {
      bidToBeat = auction.getStartPrice();
    }

    if (bidToBeat >= value)
      throw new TransactionExecutionException(ErrorCodes.BID_TOO_LOW);

    // release balance of previous bidder
    try {
      Bid bid = new Bid(schema.getAuctionBids(auctionId).getLast());
      User bidder = new User(schema.getUsers().get(bid.getBidder()));
      bidder.releaseMoney(bid.getValue());
      schema.getUsers().put(bid.getBidder(), bidder.toProtobuf());
    } catch (Exception e) {}

    user.reserveMoney(value);
    schema.getUsers().put(author, user.toProtobuf());

    Bid bid = new Bid(author, value);
    schema.getAuctionBids(auctionId).add(bid.toProtobuf());

    HashCode bidsIndexHash = schema.getAuctionBids(auctionId).getIndexHash();
    auction.setBidsIndexHash(bidsIndexHash);
    schema.getAuctions().set(auctionId, auction.toProtobuf());
  }

  @Override
  public String info() {
    return JsonSerializer.json().toJson(this);
  }

  public static Transaction fromRawTransaction(RawTransaction rawTransaction) {
    TransactionsProtos.MakeBid protoTx =
        StandardSerializers.protobuf(TransactionsProtos.MakeBid.class)
            .fromBytes(rawTransaction.getPayload());
    return new MakeBidTx((int)protoTx.getAuctionId(), protoTx.getValue());
  }
}
