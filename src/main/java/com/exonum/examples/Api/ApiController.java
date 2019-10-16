package com.exonum.examples.Api;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.serialization.json.JsonSerializer;
import com.exonum.binding.core.service.Node;
import com.exonum.examples.Schema;
import com.exonum.examples.model.User;
import com.google.common.base.Strings;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class ApiController {
  private final Node node;

  public ApiController(Node node) {
    this.node = node;
  }

  public void mountApi(Router router) {
    router.route("/v1/user").handler(this::getUser);
    router.route("/v1/users").handler(this::getUsers);
    router.route().failureHandler(this::failureHandler);
  }

  private void getUsers(RoutingContext routingContext) {
    ArrayList<UserDTO> response = node.withSnapshot(view -> {
      ArrayList<UserDTO> users = new ArrayList<>();
      Schema schema = new Schema(view);
      schema.getUsers().entries().forEachRemaining((entry) -> {
        User user = new User(entry.getValue());
        String name = user.getName();
        long balance = user.getBalance();
        long reserved = user.getReserved();
        users.add(new UserDTO(entry.getKey(), name, balance, reserved));
      });
      return users;
    });

    routingContext.response()
        .putHeader("Content-Type", "application/json")
        .end(JsonSerializer.json().toJson(response));
  }

  private void getUser(RoutingContext routingContext) {
    MultiMap params = routingContext.queryParams();
    String publicKeyString = params.get("pub_key");

    UserDTO response = node.withSnapshot(view -> {
      Schema schema = new Schema(view);
      PublicKey publicKey = PublicKey.fromHexString(publicKeyString);
      User user = new User(schema.getUsers().get(publicKey));
      return new UserDTO(publicKey, user.getName(), user.getBalance(), user.getReserved());
    });

    routingContext.response()
        .putHeader("Content-Type", "application/json")
        .end(JsonSerializer.json().toJson(response));
  }

  private static void respondBadRequest(RoutingContext rc, String description) {
    rc.response()
        .setStatusCode(HTTP_BAD_REQUEST)
        .putHeader("Content-Type", "text/plain")
        .end(description);
  }

  private void failureHandler(RoutingContext rc) {
    Throwable requestFailure = rc.failure();
    if (requestFailure != null) {
      Optional<String> badRequest = badRequestDescription(requestFailure);
      if (badRequest.isPresent()) {
        respondBadRequest(rc, badRequest.get());
      } else {
        rc.response()
            .setStatusCode(HTTP_INTERNAL_ERROR)
            .end();
      }
    } else {
      int failureStatusCode = rc.statusCode();
      rc.response()
          .setStatusCode(failureStatusCode)
          .end();
    }
  }

  /**
   * If the passed throwable corresponds to a bad request — returns an error message,
   * or {@code Optional.empty()} otherwise.
   */
  private Optional<String> badRequestDescription(Throwable requestFailure) {
    // All IllegalArgumentExceptions are considered to be caused by a bad request.
    if (requestFailure instanceof IllegalArgumentException) {
      String message = Strings.nullToEmpty(requestFailure.getLocalizedMessage());
      return Optional.of(message);
    }

    // This throwable must correspond to an internal server error.
    return Optional.empty();
  }

}
