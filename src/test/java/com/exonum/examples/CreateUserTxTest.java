package com.exonum.examples;

import com.exonum.binding.common.crypto.CryptoFunctions;
import com.exonum.binding.common.crypto.KeyPair;
import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.message.TransactionMessage;
import com.exonum.binding.core.storage.indices.ListIndex;
import com.exonum.binding.testkit.TestKit;
import com.exonum.binding.testkit.TestKitExtension;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.cryptoowls.transactions.TransactionsProtos.CreateUser;
import com.exonum.examples.transactions.CreateUserTx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class CreateUserTxTest {
  private static final KeyPair KEYS = CryptoFunctions.ed25519().generateKeyPair();

  @RegisterExtension
  TestKitExtension testKitExtension = new TestKitExtension(
      TestKit.builder().withService(ServiceModule.class));

  @Test
  void createUserTest(TestKit testKit) {
    String userName = "John";
    PublicKey userPublicKey = KEYS.getPublicKey();
    ModelProtos.User expectedUser = Helpers.createUser(userName);
    CreateUser payload = CreateUser.newBuilder().setName(userName).build();
    TransactionMessage txMessage = TransactionMessage.builder()
        .serviceId(CryptoowlsService.ID)
        .transactionId(CreateUserTx.ID)
        .payload(payload.toByteArray())
        .sign(KEYS, CryptoFunctions.ed25519());
    testKit.createBlockWithTransactions(txMessage);
    CryptoowlsSchema schema = new CryptoowlsSchema(testKit.getSnapshot());
    assert(schema.getUsers().get(userPublicKey).equals(expectedUser));
  }
}
