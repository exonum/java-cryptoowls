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
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
import com.exonum.examples.model.User;

import java.time.ZonedDateTime;

public class CreateOwlTx implements Transaction {
  public static final short ID = 2;
  private static final long BREEDING_PRICE = 10;

  private final String name;
  private final HashCode fatherId;
  private final HashCode motherId;

  public CreateOwlTx(String name, HashCode fatherId, HashCode motherId) {
    this.name = name;
    this.fatherId = fatherId;
    this.motherId = motherId;
  }

  @Override
  public String info() {
    return JsonSerializer.json().toJson(this);
  }

  @Override
  public void execute(TransactionContext transactionContext) throws TransactionExecutionException {
    PublicKey owner = transactionContext.getAuthorPk();
    Schema schema = new Schema(transactionContext.getFork());
    HashCode uniqueHash = schema.getOwls().getIndexHash();

    TimeSchema timeSchema = TimeSchema.newInstance(transactionContext.getFork());
    ZonedDateTime currentTime = timeSchema.getTime().get();

    schema.closeAuctionsIfNeeded(currentTime);

    if (fatherId.equals(motherId))
      throw new TransactionExecutionException(ErrorCodes.IDENTICAL_PARENTS);

    ModelProtos.CryptoOwl fatherProtobufOwl = schema.getOwls().get(fatherId);
    ModelProtos.CryptoOwl motherProtobufOwl = schema.getOwls().get(motherId);
    if (fatherProtobufOwl == null || motherProtobufOwl == null)
      throw new TransactionExecutionException(ErrorCodes.OWL_NOT_FOUND);

    Owl fatherOwl = new Owl(fatherProtobufOwl);
    Owl motherOwl = new Owl(motherProtobufOwl);

    if (!fatherOwl.getOwner().equals(owner) || !motherOwl.getOwner().equals(owner))
      throw new TransactionExecutionException(ErrorCodes.NOT_AUTHORIZED);

    User ownerUser = new User(schema.getUsers().get(owner));

    if (ownerUser.getBalance() < BREEDING_PRICE)
      throw new TransactionExecutionException(ErrorCodes.INSUFFICIENT_FUNDS);

    if (!fatherOwl.isBreedingPossible(currentTime) ||
        !motherOwl.isBreedingPossible(currentTime))
      throw new TransactionExecutionException(ErrorCodes.TOO_EARLY_FOR_BREEDING);

    Owl owl = new Owl(name,
        transactionContext.getAuthorPk(),
        fatherOwl.getDNA(),
        motherOwl.getDNA(),
        uniqueHash,
        currentTime
    );

    HashCode owlHash = owl.getHash();
    schema.getOwls().put(owlHash, owl.toProtobuf());
    schema.getUserOwls(owner).add(owlHash);
  }

  public static Transaction fromRawTransaction(RawTransaction rawTransaction) {
    TransactionsProtos.CreateOwl tx =
        StandardSerializers.protobuf(TransactionsProtos.CreateOwl.class)
            .fromBytes(rawTransaction.getPayload());

    return new CreateOwlTx(
        tx.getName(),
        Helpers.hashFromProtobuf(tx.getFatherId()),
        Helpers.hashFromProtobuf(tx.getMotherId()));
  }
}
