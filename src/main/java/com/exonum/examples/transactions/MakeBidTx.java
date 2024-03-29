/*
 * Copyright 2019 The Exonum Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exonum.examples.transactions;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.transaction.RawTransaction;
import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.TransactionContext;
import com.exonum.binding.core.transaction.TransactionExecutionException;
import com.exonum.examples.Helpers;
import com.exonum.examples.Schema;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
import com.exonum.examples.model.Auction;
import com.exonum.examples.model.Bid;
import com.exonum.examples.model.User;

import java.time.ZonedDateTime;

public class MakeBidTx implements Transaction {
  public static final short ID = 3;

  private int auctionId;
  private long value;

  private MakeBidTx(int auctionId, long value) {
    this.auctionId = auctionId;
    this.value = value;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    ZonedDateTime currentTime = Helpers.getCurrentTime(transactionContext.getFork());
    Schema schema = new Schema(transactionContext.getFork());
    PublicKey author = transactionContext.getAuthorPk();

    schema.closeAuctionsIfNeeded(currentTime);

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

    // release balance of previous bidder (if there is some)
    try {
      Bid bid = new Bid(schema.getAuctionBids(auctionId).getLast());
      User bidder = new User(schema.getUsers().get(bid.getBidder()));
      bidder.releaseMoney(bid.getValue());
      schema.getUsers().put(bid.getBidder(), bidder.toProtobuf());
    } catch (Exception ignored) {}

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
