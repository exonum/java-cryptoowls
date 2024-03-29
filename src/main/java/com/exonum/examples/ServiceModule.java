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

import com.exonum.binding.core.service.AbstractServiceModule;
import com.google.inject.Singleton;
import org.pf4j.Extension;

/**
 * A service module defines bindings required to create an instance of {@link Service}.
 */
@Extension
public final class ServiceModule extends AbstractServiceModule {

  @Override
  protected void configure() {
    bind(com.exonum.binding.core.service.Service.class).to(Service.class).in(Singleton.class);
    bind(com.exonum.binding.core.service.TransactionConverter.class).to(TransactionConverter.class);
  }
}
