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
