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
import com.exonum.examples.model.Owl;
import com.exonum.examples.Schema;
import com.exonum.examples.model.User;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;

import java.time.ZonedDateTime;

public class CreateUserTx implements Transaction {
  public static final short ID = 0;

  private final String userName;

  private CreateUserTx(String userName) {
    this.userName = userName;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    ZonedDateTime currentTime = Helpers.getCurrentTime(transactionContext.getFork());
    PublicKey userPK = transactionContext.getAuthorPk();

    Schema schema = new Schema(transactionContext.getFork());

    schema.closeAuctionsIfNeeded(currentTime);

    if (schema.getUsers().get(userPK) != null) {
      throw new TransactionExecutionException(ErrorCodes.ALREADY_REGISTERED);
    }

    User user = new User(userName);
    schema.getUsers().put(userPK, user.toProtobuf());


    String initialFatherName = String.format("%s's Adam", userName);
    String initialMotherName = String.format("%s's Eve", userName);
    HashCode initialFather = addInitialOwl(initialFatherName, userPK, schema, currentTime);
    HashCode initialMother = addInitialOwl(initialMotherName, userPK, schema, currentTime);
    schema.getUserOwls(userPK).add(initialFather);
    schema.getUserOwls(userPK).add(initialMother);
  }

  private HashCode addInitialOwl(String name, PublicKey authorPK, Schema schema, ZonedDateTime currentTime) {
    HashCode uniqueHash = schema.getOwls().getIndexHash();
    Owl owl = new Owl(name, authorPK, uniqueHash, currentTime);
    schema.getOwls().put(owl.getHash(), owl.toProtobuf());
    return owl.getHash();
  }

  @Override
  public String info() {
    return JsonSerializer.json().toJson(this);
  }

  public static Transaction fromRawTransaction(RawTransaction rawTransaction) {
    TransactionsProtos.CreateUser payload =
        StandardSerializers.protobuf(TransactionsProtos.CreateUser.class)
            .fromBytes(rawTransaction.getPayload());

    return new CreateUserTx(payload.getName());
  }
}
