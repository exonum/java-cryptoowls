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

package com.exonum.examples;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.core.storage.database.View;
import com.exonum.binding.core.transaction.TransactionExecutionException;
import com.exonum.binding.time.TimeSchema;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.transactions.ErrorCodes;
import com.google.protobuf.Timestamp;
import com.google.protobuf.ByteString;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Helpers {
  public static ZonedDateTime getCurrentTime(View view) throws TransactionExecutionException {
    TimeSchema timeSchema = TimeSchema.newInstance(view);
    if (!timeSchema.getTime().isPresent())
      throw new TransactionExecutionException(ErrorCodes.TIME_NOT_AVAILABLE,
          "Time service not yet available");

    return timeSchema.getTime().get();
  }

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
