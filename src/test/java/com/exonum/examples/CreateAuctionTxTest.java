//package com.exonum.examples;
//
//import com.exonum.binding.common.crypto.CryptoFunctions;
//import com.exonum.binding.common.crypto.KeyPair;
//import com.exonum.binding.common.crypto.PublicKey;
//import com.exonum.binding.common.message.TransactionMessage;
//import com.exonum.binding.testkit.TestKit;
//import com.exonum.binding.testkit.TestKitExtension;
//import com.exonum.examples.cryptoowls.model.ModelProtos;
//import com.exonum.examples.cryptoowls.transactions.TransactionsProtos;
//import com.exonum.examples.transactions.CreateAuctionTx;
//import com.exonum.examples.transactions.CreateUserTx;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//public class CreateAuctionTxTest {
//  private static final KeyPair KEYS = CryptoFunctions.ed25519().generateKeyPair();
//
//  @RegisterExtension
//  TestKitExtension testKitExtension = new TestKitExtension(
//      TestKit.builder().withService(ServiceModule.class));
//
//  @Test
//  void createAuctionTest(TestKit testKit) {
//    Helpers.createAuction(KEYS.getPublicKey(), 100, new Timestamp())
//    TransactionMessage txMessage = TransactionMessage.builder()
//        .serviceId(CryptoowlsService.ID)
//        .transactionId(CreateAuctionTx.ID)
//        .payload()
//    testKit.createBlockWithTransactions(txMessage);
//    CryptoowlsSchema schema = new CryptoowlsSchema(testKit.getSnapshot());
//    assert(schema.getUsers().get(userPublicKey).equals(expectedUser));
//  }
//}
