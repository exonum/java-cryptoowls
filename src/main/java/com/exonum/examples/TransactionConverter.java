/*
 * Copyright 2018 The Exonum Team
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

package com.exonum.examples;

import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.RawTransaction;
import com.exonum.examples.transactions.CreateAuctionTx;
import com.exonum.examples.transactions.CreateOwlTx;
import com.exonum.examples.transactions.CreateUserTx;
import com.google.common.collect.ImmutableMap;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@code MyTransactionConverter} converts raw transactions of {@link Service}
 * into {@linkplain Transaction executable transactions} of this service.
 */
public final class TransactionConverter implements com.exonum.binding.core.service.TransactionConverter {
  private static final ImmutableMap<Short, Function<RawTransaction, Transaction>>
      TRANSACTION_FACTORIES =
      ImmutableMap.of(
          CreateUserTx.ID, CreateUserTx::fromRawTransaction,
          CreateAuctionTx.ID, CreateAuctionTx::fromRawTransaction,
          CreateOwlTx.ID, CreateOwlTx::fromRawTransaction
      );

  @Override
  public Transaction toTransaction(RawTransaction rawTransaction) {
    checkServiceId(rawTransaction);

    short txId = rawTransaction.getTransactionId();

    return TRANSACTION_FACTORIES.getOrDefault(txId, (m) -> {
      throw new IllegalArgumentException("Unknown transaction id: " + txId);
    }).apply(rawTransaction);
  }

  static void checkServiceId(RawTransaction transaction) {
    short serviceId = transaction.getServiceId();
    checkArgument(serviceId == Service.ID,
        "Wrong service id (%s), must be %s",
        serviceId, Service.ID);
  }

}
