package com.exonum.examples;

import com.exonum.binding.common.crypto.CryptoFunctions;
import com.exonum.binding.common.crypto.KeyPair;
import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.message.TransactionMessage;
import com.exonum.binding.testkit.FakeTimeProvider;
import com.exonum.binding.testkit.TestKit;
import com.exonum.binding.testkit.TestKitExtension;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos.CreateUser;
import com.exonum.examples.model.User;
import com.exonum.examples.transactions.CreateUserTx;
import com.google.common.collect.Iterators;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class CreateUserTxTest {
  private static final KeyPair KEYS = CryptoFunctions.ed25519().generateKeyPair();
  private static final ZonedDateTime EXPECTED_TIME = ZonedDateTime.now(ZoneOffset.UTC);
  private static final FakeTimeProvider timeProvider = FakeTimeProvider.create(EXPECTED_TIME);

  @RegisterExtension
  TestKitExtension testKitExtension = new TestKitExtension(
      TestKit.builder().withTimeService(timeProvider).withService(ServiceModule.class));

  @Test
  void createUserTest(TestKit testKit) {
    testKit.createBlock();
    testKit.createBlock();
    String userName = "John";
    PublicKey userPublicKey = KEYS.getPublicKey();
    ModelProtos.User expectedUser = new User(userName).toProtobuf();
    CreateUser payload = CreateUser.newBuilder().setName(userName).build();
    TransactionMessage txMessage = TransactionMessage.builder()
        .serviceId(Service.ID)
        .transactionId(CreateUserTx.ID)
        .payload(payload.toByteArray())
        .sign(KEYS, CryptoFunctions.ed25519());
    testKit.createBlockWithTransactions(txMessage);
    Schema schema = new Schema(testKit.getSnapshot());
    assert(schema.getUsers().get(userPublicKey).equals(expectedUser));
    assert(Iterators.size(schema.getOwls().keys()) == 2);
    assert Iterators.size(schema.getUserOwls(userPublicKey).hashes()) == 2;
    schema.getUserOwls(userPublicKey).iterator().forEachRemaining((entry) -> {
      assert schema.getOwls().get(entry.getValue()) != null;
    });
  }
}
