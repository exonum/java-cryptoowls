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

import com.exonum.binding.core.service.AbstractService;
import com.exonum.binding.core.service.TransactionConverter;
import com.exonum.binding.core.storage.database.Fork;
import com.exonum.binding.core.storage.database.View;
import com.exonum.examples.Api.ApiController;
import com.google.inject.Inject;
import io.vertx.ext.web.Router;

import java.util.Optional;

public final class Service extends AbstractService {

  public static final short ID = 42;
  static final String NAME = "java-cryptoowls";
  private static final String INITIAL_SERVICE_CONFIGURATION = "{ \"version\": 0.1 }";

  @Inject
  public Service(TransactionConverter transactionConverter) {
    super(ID, NAME, transactionConverter);
  }

  @Override
  protected com.exonum.binding.core.service.Schema createDataSchema(View view) {
    return new Schema(view);
  }

  @Override
  public Optional<String> initialize(Fork fork) {
    return Optional.of(INITIAL_SERVICE_CONFIGURATION);
  }

  @Override
  public void createPublicApiHandlers(com.exonum.binding.core.service.Node node, Router router) {
    ApiController controller = new ApiController(node);
    controller.mountApi(router);
  }
}
