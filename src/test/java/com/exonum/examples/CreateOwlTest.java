package com.exonum.examples;

import com.exonum.binding.common.crypto.CryptoFunctions;
import com.exonum.binding.common.crypto.KeyPair;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.message.TransactionMessage;
import com.exonum.binding.core.storage.indices.ValueSetIndexProxy;
import com.exonum.binding.testkit.FakeTimeProvider;
import com.exonum.binding.testkit.TestKit;
import com.exonum.binding.testkit.TestKitExtension;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
import com.exonum.examples.model.Owl;
import com.exonum.examples.transactions.CreateOwlTx;
import com.exonum.examples.transactions.CreateUserTx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;

class CreateOwlTest {
  private static final KeyPair KEYS = CryptoFunctions.ed25519().generateKeyPair();
  private static final ZonedDateTime EXPECTED_TIME = ZonedDateTime.now(ZoneOffset.UTC);
  private static FakeTimeProvider timeProvider = FakeTimeProvider.create(EXPECTED_TIME);

  @RegisterExtension
  TestKitExtension testKitExtension;

  {
    testKitExtension = new TestKitExtension(
        TestKit.builder().withTimeService(timeProvider).withService(ServiceModule.class));
  }

  @Test
  void createOwlTxTest(TestKit testKit) {
    testKit.createBlock();
    testKit.createBlock();
    String owlName = "John";

    TransactionsProtos.CreateUser createUserTxPayload = TransactionsProtos.CreateUser.newBuilder()
        .setName("Adam")
        .build();
    TransactionMessage createUserTx = TransactionMessage.builder()
        .serviceId(Service.ID)
        .transactionId(CreateUserTx.ID)
        .payload(createUserTxPayload.toByteArray())
        .sign(KEYS, CryptoFunctions.ed25519());
    testKit.createBlockWithTransactions(createUserTx);

    Schema schema = new Schema(testKit.getSnapshot());
    Iterator<ValueSetIndexProxy.Entry<HashCode>> initialUserOwls =
        schema.getUserOwls(KEYS.getPublicKey()).iterator();
    assert initialUserOwls.hasNext();
    HashCode initialFather = initialUserOwls.next().getValue();
    int fatherDNA = schema.getOwls().get(initialFather).getDna();
    assert initialUserOwls.hasNext();
    HashCode initialMother = initialUserOwls.next().getValue();
    int motherDNA = schema.getOwls().get(initialMother).getDna();
    assert !initialUserOwls.hasNext();

    Duration amountToAdd = Duration.ofMinutes(10);
    timeProvider.addTime(amountToAdd);
    ZonedDateTime expectedTime = EXPECTED_TIME.plus(amountToAdd);

    Owl expectedOwl = new Owl(owlName, KEYS.getPublicKey(), fatherDNA, motherDNA, schema.getOwls().getIndexHash(), expectedTime);

    TransactionsProtos.CreateOwl tx = TransactionsProtos.CreateOwl.newBuilder()
        .setName(owlName)
        .setFatherId(Helpers.hashToProtobuf(initialFather))
        .setMotherId(Helpers.hashToProtobuf(initialMother))
        .build();
    TransactionMessage createOwlTx = TransactionMessage.builder()
        .serviceId(Service.ID)
        .transactionId(CreateOwlTx.ID)
        .payload(tx.toByteArray())
        .sign(KEYS, CryptoFunctions.ed25519());

    testKit.createBlockWithTransactions(createOwlTx);

    schema = new Schema(testKit.getSnapshot());

    assert schema.getOwls().get(expectedOwl.getHash()).equals(expectedOwl.toProtobuf());
  }
}
