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

  private CreateAuctionTx(HashCode owlHash, long startPrice, long duration) {
    this.owlHash = owlHash;
    this.startPrice = startPrice;
    this.duration = duration;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    ZonedDateTime currentTime = Helpers.getCurrentTime(transactionContext.getFork());
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
