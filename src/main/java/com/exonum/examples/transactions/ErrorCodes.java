package com.exonum.examples.transactions;

public final class ErrorCodes {
  static final byte TOO_EARLY_FOR_BREEDING = 0;
  static final byte INSUFFICIENT_FUNDS = 1;
  static final byte NOT_USER_PROPERTY = 2;
  static final byte IDENTICAL_PARENTS = 3;
  static final byte ALREADY_REGISTERED = 4;
  static final byte USER_NOT_FOUND = 5;
  static final byte OWL_NOT_FOUND = 6;
  static final byte OWL_NOT_OWNED = 7;
  static final byte OWL_ALREADY_AUCTIONED = 8;
  static final byte AUCTION_NOT_FOUND = 9;
  static final byte AUCTION_CLOSED = 10;
  static final byte BID_TOO_LOW = 11;
  static final byte NOT_AUTHORIZED = 12;
  static final byte SELF_BIDDING = 13;
  public static final byte TIME_NOT_AVAILABLE = 14;

  private ErrorCodes() {}
}
