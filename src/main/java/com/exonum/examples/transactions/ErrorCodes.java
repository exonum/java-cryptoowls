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

package com.exonum.examples.transactions;

public final class ErrorCodes {
  static final byte TOO_EARLY_FOR_BREEDING = 0;
  static final byte INSUFFICIENT_FUNDS = 1;
  static final byte IDENTICAL_PARENTS = 2;
  static final byte ALREADY_REGISTERED = 3;
  static final byte USER_NOT_FOUND = 4;
  static final byte OWL_NOT_FOUND = 5;
  static final byte OWL_NOT_OWNED = 6;
  static final byte OWL_ALREADY_AUCTIONED = 7;
  static final byte AUCTION_NOT_FOUND = 8;
  static final byte AUCTION_CLOSED = 9;
  static final byte BID_TOO_LOW = 10;
  static final byte NOT_AUTHORIZED = 11;
  static final byte SELF_BIDDING = 12;
  public static final byte TIME_NOT_AVAILABLE = 13;

  private ErrorCodes() {}
}
