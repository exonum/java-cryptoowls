package com.exonum.examples.transactions;

import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.transaction.Transaction;
import com.exonum.binding.core.transaction.TransactionContext;
import com.exonum.binding.core.transaction.TransactionExecutionException;

public class CreateAuctionTx implements Transaction {
  public static final short ID = 2;

  private final HashCode owl;
  private final long startPrice;
  private final long duration;

  public CreateAuctionTx(HashCode owl, long startPrice, long duration) {
    this.owl = owl;
    this.startPrice = startPrice;
    this.duration = duration;
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
  }

  @Override
  public String info() {
    return JsonSerializer.json().toJson(this);
  }
}
