package com.exonum.examples.transactions;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.transaction.RawTransaction;
import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.TransactionContext;
import com.exonum.binding.core.transaction.TransactionExecutionException;
import com.exonum.binding.time.TimeSchema;
import com.exonum.examples.Helpers;
import com.exonum.examples.Schema;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
import com.exonum.examples.model.Auction;
import com.exonum.examples.model.Owl;
import com.google.common.collect.Iterators;

import java.time.ZonedDateTime;

public class CreateAuctionTx implements Transaction {
  public static final short ID = 1;

  private final HashCode owlHash;
  private final long startPrice;
  private final long duration;

  public CreateAuctionTx(HashCode owlHash, long startPrice, long duration) {
    this.owlHash = owlHash;
    this.startPrice = startPrice;
    this.duration = duration;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    ZonedDateTime currentTime =
        TimeSchema.newInstance(transactionContext.getFork()).getTime().get();
    PublicKey owner = transactionContext.getAuthorPk();
    Schema schema = new Schema(transactionContext.getFork());

    schema.closeAuctionsIfNeeded(currentTime);

    if (schema.getUsers().get(owner) == null)
      throw new TransactionExecutionException(ErrorCodes.USER_NOT_FOUND);

    if (schema.getOwls().get(owlHash) == null)
      throw new TransactionExecutionException(ErrorCodes.OWL_NOT_FOUND);

    Owl owl = new Owl(schema.getOwls().get(owlHash));
    if (!owl.getOwner().equals(owner))
      throw new TransactionExecutionException(ErrorCodes.OWL_NOT_OWNED);

    if (schema.getOwlAuctions().get(owlHash) != null)
      throw new TransactionExecutionException(ErrorCodes.OWL_ALREADY_AUCTIONED);

    HashCode bidsIndexHash = HashCode.fromBytes(new byte[32]);

    Auction auction =
        new Auction(owner, owlHash, startPrice, duration, currentTime, bidsIndexHash, false);

    int auctionId = Iterators.size(schema.getAuctions().iterator());
    schema.getAuctions().add(auction.toProtobuf());
    schema.getOwlAuctions().put(owlHash, auctionId);
  }

  @Override
  public String info() {
    return JsonSerializer.json().toJson(this);
  }

  public static Transaction fromRawTransaction(RawTransaction rawTransaction) {
    TransactionsProtos.CreateAuction payload =
        StandardSerializers.protobuf(TransactionsProtos.CreateAuction.class)
            .fromBytes(rawTransaction.getPayload());
    HashCode owl = Helpers.hashFromProtobuf(payload.getOwlId());
    return new CreateAuctionTx(owl, payload.getStartPrice(), payload.getDuration());
  }
}
