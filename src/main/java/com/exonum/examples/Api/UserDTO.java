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

package com.exonum.examples.Api;

import com.exonum.binding.common.crypto.PublicKey;
import com.google.gson.annotations.SerializedName;

class UserDTO {
  @SerializedName("public_key")
  private PublicKey publicKey;
  private String name;
  private long balance;
  private long reserved;

  UserDTO(PublicKey publicKey, String name, long balance, long reserved) {
    this.publicKey = publicKey;
    this.name = name;
    this.balance = balance;
    this.reserved = reserved;
  }
}
