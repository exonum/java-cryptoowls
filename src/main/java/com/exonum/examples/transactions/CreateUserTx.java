package com.exonum.examples.transactions;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.transaction.RawTransaction;
import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.TransactionContext;
import com.exonum.binding.core.transaction.TransactionExecutionException;
import com.exonum.examples.CryptoowlsSchema;
import com.exonum.examples.Helpers;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;

public class CreateUserTx implements Transaction {
  public static final short ID = 0;
  private static final long INITIAL_USER_BALANCE = 100;
  private final String name;

  public CreateUserTx(String name) {
    this.name = name;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    PublicKey authorPK = transactionContext.getAuthorPk();

    CryptoowlsSchema schema = new CryptoowlsSchema(transactionContext.getFork());

    ModelProtos.User user = Helpers.createUser(name);
    schema.getUsers().put(authorPK, user);
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
