package com.exonum.examples;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.google.protobuf.Timestamp;
import com.google.protobuf.ByteString;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Helpers {
  public static Timestamp zdtToTimestamp(ZonedDateTime value) {
    return Timestamp.newBuilder()
        .setSeconds(value.toEpochSecond())
        .setNanos(value.getNano())
        .build();
  }

  public static ZonedDateTime timestampToZdt(Timestamp value) {
    Instant instant = Instant.ofEpochSecond(value.getSeconds(), value.getNanos());
    return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public static ModelProtos.PublicKey publicKeyToProtobuf(PublicKey publicKey) {
    return ModelProtos.PublicKey.newBuilder()
        .setData(ByteString.copyFrom(publicKey.toBytes()))
        .build();
  }

  public static PublicKey publicKeyFromProtobuf(ModelProtos.PublicKey protoPK) {
    return PublicKey.fromBytes(protoPK.getData().toByteArray());
  }

  public static ModelProtos.Hash hashToProtobuf(HashCode hash) {
    return ModelProtos.Hash.newBuilder()
        .setData(ByteString.copyFrom(hash.asBytes()))
        .build();
  }

  public static HashCode hashFromProtobuf(ModelProtos.Hash protoHash) {
    return HashCode.fromBytes(protoHash.getData().toByteArray());
  }
}
